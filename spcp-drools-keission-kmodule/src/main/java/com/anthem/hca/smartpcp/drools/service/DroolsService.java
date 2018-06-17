package com.anthem.hca.smartpcp.drools.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.drools.rules.Rules;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;

@Service
public class DroolsService {

	@Autowired
	private KieContainer kieContainer;

	@Autowired
	private KieSession kieSession;

	private static final Logger logger = LogManager.getLogger(DroolsService.class);

	public void fireRules(Rules rules) throws DroolsParseException {
		AgendaGroup group = rules.getAgendaGroup();

		logger.info("Firing Rules for " + group.name() + "...");
		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		// check whether Rules have been updated by Drools
		if (rules.isFallbackRequired()) {
			logger.info("Rules not updated after first fire. Fallback Required...");
			rules.setFallback(); // Reset the Rules to default
			logger.info("Rules have been reset to Default");

			// Fire the Rules again with default values
			logger.info("Firing Fallback Rules for " + group.name() + "...");
			try {
				kieSession.insert(rules);
				kieSession.getAgenda().getAgendaGroup(group.getValue()).setFocus();
				kieSession.fireAllRules();
				kieSession.delete(kieSession.getFactHandle(rules));
			} catch (Exception e) {
				throw new DroolsParseException(e.getMessage());
			}
		}
		else {
			logger.info("Rules are updated after first fire. Fallback NOT Required...");
		}

		logger.info("Rules extracted for " + group.name());
	}

	@Bean
	public KieContainer getKieContainer() {
		return KieServices.Factory.get().getKieClasspathContainer();
	}

	@Bean
	public KieSession getKieSession() {
		return kieContainer.newKieSession("ksession-dtables");
	}

}
