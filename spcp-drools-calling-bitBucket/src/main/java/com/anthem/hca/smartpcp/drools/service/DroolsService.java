package com.anthem.hca.smartpcp.drools.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.io.RulesInputPayload;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.rules.RulesFactory;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.util.GlobalKieSession;

@Service
@RefreshScope
public class DroolsService implements ApplicationListener<EnvironmentChangeEvent>  {
	
	@Value("${user.name}")
	private String userName;
	
	@Value("${user.pass}")
	private String userPass;
	
	@Autowired
	private Environment environment;
	
	private List<String> fileUrls;
	
	@Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
		fileUrls = Arrays.asList(environment.getProperty("rule.files").split(","));
		getKieSession();
    }
	

	public enum AgendaGroup {

		ACTUAL_FALLBACK("RULECONF"),
		SMARTPCP("SMARTPCP"),
		AFFINITY_PROVIDER_VALIDATION("AFFINITY"),
		MDO_PROVIDER_VALIDATION("MDO"),
		MDO_POOLING("MDO-POOLING"),
		MDO_SCORING("MDO-SCORING");

		private String value;

		public String getValue() {
			return value;
		}

		private AgendaGroup(String value) {
			this.value = value;
		}

	}

	private static final Logger logger = LogManager.getLogger(DroolsService.class);

	public boolean isFallbackRequired(RulesInputPayload payload) throws DroolsParseException {
		logger.info("Firing Rules for " + AgendaGroup.ACTUAL_FALLBACK.name() + "...");

		Rules rules = RulesFactory.initRule(RulesFactory.createRule(AgendaGroup.ACTUAL_FALLBACK), payload.getMember());
		KieSession kieSession = GlobalKieSession.getInstance().getKieSession();
		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup(AgendaGroup.ACTUAL_FALLBACK.getValue()).setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		logger.info("Rules extracted for " + AgendaGroup.ACTUAL_FALLBACK.name());
		return rules.isFallbackRequired();
	}

	public Rules fireRulesFor(AgendaGroup group, RulesInputPayload payload, boolean fallback) throws DroolsParseException {
		logger.info("Firing Rules for " + group.name() + "...");

		Rules rules = RulesFactory.initRule(RulesFactory.createRule(group), payload.getMember(), fallback);
		KieSession kieSession = GlobalKieSession.getInstance().getKieSession();
		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		logger.info("Rules extracted for " + group.name());
		return rules;
	}
	
	private KieSession getKieSession() {//ssh://git@bitbucket.anthem.com:7999/soadckrad/spcp-configuration.git
		String fileUrl = "https://bitbucket.anthem.com/projects/SOADCKRAD/repos/spcp-configuration/raw/properties/spcp-drools/rules/";
		String refHead = "?at=refs/heads/sit";
		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		String encoded = Base64.getEncoder().encodeToString((userName + ":" + userPass).getBytes(StandardCharsets.UTF_8));
		HttpURLConnection connection;
		for(int i=0; i<fileUrls.size(); i++) {
			String url = fileUrl + fileUrls.get(i) + refHead;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestProperty("Authorization", "Basic " + encoded);
				InputStream inputStream = connection.getInputStream();
				Resource resource = ResourceFactory.newInputStreamResource(inputStream);
				kieFileSystem.write("src/main/resources/rules/rules_"+i+".xls", resource);
			} catch (IOException e) {
				logger.error("Connection error" + e);
			}
		}
		
		KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
		kieBuilder.buildAll();
		KieRepository kieRepository = kieServices.getRepository();
		KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
		KieSession kieSessionInstance = GlobalKieSession.getInstance().getKieSession();
		if(kieSessionInstance != null) {
			kieSessionInstance.destroy();
		}
		KieSession newKieSession = kieContainer.newKieSession();
		GlobalKieSession.getInstance().setKieSession(newKieSession);
		return newKieSession;
	}

}
