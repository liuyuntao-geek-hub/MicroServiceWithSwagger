package com.anthem.hca.smartpcp.drools.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.preprocessor.RulesPreprocessor;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.RefreshEventException;
import com.anthem.hca.smartpcp.drools.util.StaticLoggerMessages;

/**
 * The DroolsService class is the Service Layer implementation of MDO Pooling Rules.
 * It invokes the DroolsService to fire the specific Rules and returns a Rules object
 * containing all the parameters for MDO Pooling and Dummy PCP.
 *
 * @author  Nitya Khamari (AF43078), Saptarshi Dey (AF66853)
 * @version 1.8
 */

@Service
public class DroolsService implements ApplicationListener<EnvironmentChangeEvent> {

	@Autowired
	private Environment environment;

	@Autowired
	private RulesPreprocessor preProcessor;

	private List<String> fileUrls;
	private List<String> matrixFileUrls;
	private List<InputStream> matrixFiles;
	private String kieFileLocation;

	private KieContainer kieContainer;
	private KieSession kieSession;

	private static final Logger logger = LoggerFactory.getLogger(DroolsService.class);

	/**
	 * This method is an Event listener that is triggered on Application Startup and Refresh.
	 * 
	 * @param  event                  Environment Change Event
	 * @return                        None
	 * @throws RefreshEventException  During any Runtime Exception
	 * @see    EnvironmentChangeEvent
	 * @see    GlobalKieSession
	 * @see    KieSession
	 */
	@Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
		fileUrls = Arrays.asList(environment.getProperty("spcp.business.rule.files").split(","));
		kieFileLocation = environment.getProperty("spcp.drools.virtual.filesystem.location") + "/";
		matrixFileUrls = Arrays.asList(environment.getProperty("spcp.business.rule.matrixFiles").split(","));
		matrixFiles = new ArrayList<>(matrixFileUrls.size());

		// Assigning the current session to a variable so that it can be disposed off
		// once the new session has been created properly
		KieSession oldSession = kieSession;

		try {
			kieContainer = getKieContainer();
			kieSession = kieContainer.newKieSession();

			if (kieSession == null) {
				throw new RefreshEventException("kieSession is null");
			}

			preProcessor.createMatrix(matrixFiles);	// create Matrix data for all the Agenda Groups
		}
		catch (IOException e) {
			throw new RefreshEventException(e.getMessage());
		}
		finally {
			if(oldSession != null) {
				logger.debug(StaticLoggerMessages.KIESESSION_DISPOSE);
				oldSession.dispose();
			}
		}
    }

	/**
	 * This method downloads all the Business Rule files from spcp-business-rules Service.
	 * 
	 * @param              None
	 * @return             The List of Resources
	 * @throws IOException When unable to download the files
	 * @see    ResponseEntity
	 * @see    Resource
	 */
    private List<Resource> getRuleFiles() throws IOException {
    	List<Resource> files = new ArrayList<>();

    	String msg = null;
		if (fileUrls != null) {
			for(int fileIndex = 0; fileIndex < fileUrls.size(); fileIndex++) {
				Resource r = ResourceFactory.newFileResource(kieFileLocation + fileUrls.get(fileIndex));

				if (r.getInputStream() == null) {
					msg = StaticLoggerMessages.CONTENT_ERROR + fileUrls.get(fileIndex);
					logger.error(msg);
					throw new IOException(msg);
				}
				else {
					files.add(r);
					msg = StaticLoggerMessages.FILE_DOWNLOADED + fileUrls.get(fileIndex);
					logger.debug(msg);
				}
			}
		}

		return files;
    }

	/**
	 * This method creates a KieServices object from Factory and returns it.
	 * 
	 * @param  None
	 * @return The Kie Services object
	 * @see    KieServices
	 * @see    KieServices.Factory
	 */
    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

	/**
	 * This method creates a KieFileSystem and writes all the Excel files to it.
	 * 
	 * @param              None
	 * @return             The Kie File System
	 * @throws IOException When unable to get the Rule files from Business Service
	 * @see    KieFileSystem
	 */
    private KieFileSystem getKieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();

        int fileIndex = 0;
        List<Resource> files = getRuleFiles();

        // Select the files required for creating the Matrix
        for (Resource data: files) {
        	String fileName = fileUrls.get(fileIndex++);
        	if (matrixFileUrls.contains(fileName)) {
        		matrixFiles.add(data.getInputStream());
        	}
        }

        // Write all the files to Kie FileSystem that were downloaded from the Business Rules service
        fileIndex = 0;
        for (Resource data: files) {
			kieFileSystem.write(kieFileLocation + fileUrls.get(fileIndex++), data);
        }

        return kieFileSystem;
    }

	/**
	 * This method creates a new KieContainer and returns it to the caller.
	 * 
	 * @param              None
	 * @return             The Kie Container 
	 * @throws IOException When unable to get the Rule files from Business Service
	 * @see    KieContainer
	 * @see    KieBuilder
	 * @see    KieRepository
	 */
    private KieContainer getKieContainer() throws IOException {
        final KieRepository kieRepository = getKieServices().getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);

        KieBuilder kieBuilder = getKieServices().newKieBuilder(getKieFileSystem()); 
        kieBuilder.buildAll();

        return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
    }

	/**
	 * This method fires all rules in the Rules object passed as a parameter and
	 * updates the object with the post-fired attributes.
	 * 
	 * @param  rules    The initial state of Rules object
	 * @param  fireOnce Boolean value to indicate whether Rules to be fired once or twice
	 * @return          None
	 * @throws DroolsParseException When parsing error occurs during reading the Excel rule files
	 * @see    AgendaGroup
	 * @see    AbstractRules
	 * @see    KieSession
	 * @see    GlobalKieSession
	 */
	public void fireRules(AbstractRules rules, boolean fireOnce) throws DroolsParseException {
		AgendaGroup group = rules.getAgendaGroup();

		String groupName = group.name();
		String msg = StaticLoggerMessages.FIRE_RULES + groupName;
		logger.debug(msg);
		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		// More than one fire required
		if (!fireOnce && rules.isFallbackRequired()) {
			rules.setFallback(); // Reset the Rules to default

			// Fire the Rules again with default values
			try {
				kieSession.insert(rules);
				kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
				kieSession.fireAllRules();
				kieSession.delete(kieSession.getFactHandle(rules));
			} catch (Exception e) {
				throw new DroolsParseException(e.getMessage());
			}
		}
	}

	/**
	 * This method fires all rules in the Rules object passed as a parameter and
	 * updates the object with the post-fired attributes.
	 * 
	 * @param  rules    The initial state of Rules object
	 * @param  fireOnce Boolean value to indicate whether Rules to be fired once or twice
	 * @return          None
	 * @throws DroolsParseException When parsing error occurs during reading the Excel rule files
	 * @see    AgendaGroup
	 * @see    AbstractRules
	 * @see    KieSession
	 * @see    GlobalKieSession
	 */
	public void fireRulesSynchronized(AbstractRules rules, boolean fireOnce) throws DroolsParseException {
		AgendaGroup group = rules.getAgendaGroup();

		String groupName = group.name();
		String msg = StaticLoggerMessages.FIRE_RULES + groupName;
		logger.debug(msg);
		synchronized(kieSession) {
			try {
				kieSession.insert(rules);
				kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
				kieSession.fireAllRules();
				kieSession.delete(kieSession.getFactHandle(rules));
			} catch (Exception e) {
				throw new DroolsParseException(e.getMessage());
			}			
		}

		// More than one fire required
		if (!fireOnce && rules.isFallbackRequired()) {
			rules.setFallback(); // Reset the Rules to default

			// Fire the Rules again with default values
			synchronized(kieSession) {
				try {
					kieSession.insert(rules);
					kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
					kieSession.fireAllRules();
					kieSession.delete(kieSession.getFactHandle(rules));
				} catch (Exception e) {
					throw new DroolsParseException(e.getMessage());
				}
			}
		}
	}

	/**
	 * This method fires all rules in the Rules object passed as a parameter and
	 * updates the object with the post-fired attributes.
	 * 
	 * @param  rules    The initial state of Rules object
	 * @param  fireOnce Boolean value to indicate whether Rules to be fired once or twice
	 * @return          None
	 * @throws DroolsParseException When parsing error occurs during reading the Excel rule files
	 * @see    AgendaGroup
	 * @see    AbstractRules
	 * @see    KieSession
	 * @see    GlobalKieSession
	 */
	public void fireRulesSessionPerRequest(AbstractRules rules, boolean fireOnce) throws DroolsParseException {
		AgendaGroup group = rules.getAgendaGroup();
		KieSession newSession = kieContainer.newKieSession();

		String groupName = group.name();
		String msg = StaticLoggerMessages.FIRE_RULES + groupName;
		logger.debug(msg);
		try {
			newSession.insert(rules);
			newSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
			newSession.fireAllRules();
			newSession.delete(newSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}			

		// More than one fire required
		if (!fireOnce && rules.isFallbackRequired()) {
			rules.setFallback(); // Reset the Rules to default

			// Fire the Rules again with default values
			try {
				newSession.insert(rules);
				newSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
				newSession.fireAllRules();
				newSession.delete(newSession.getFactHandle(rules));
			} catch (Exception e) {
				throw new DroolsParseException(e.getMessage());
			}
		}

		newSession.dispose();

	}

}
