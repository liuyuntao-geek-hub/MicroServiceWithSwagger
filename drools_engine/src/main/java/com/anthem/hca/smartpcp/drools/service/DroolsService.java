package com.anthem.hca.smartpcp.drools.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.anthem.hca.smartpcp.drools.model.Rules;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;


/**
 *
 */
@Service
public class DroolsService {

	@Autowired
	private KieContainer kieContainer;

	@Autowired
	private KieSession kieSession;
	
	private static final Logger logger = LogManager.getLogger(DroolsService.class);
	/**
	 * @param rules
	 * @return rules
	 * invoking this method to get actual or fall back logic
	 */
	public Rules fireRulesForActualOrFallback(Rules rules) throws DroolsParseException {
			
		logger.info("Firing RULECONF to determine whether Actual or Fallback required");

		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup("RULECONF").setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		return rules;
	}
	
	/**
	 * @param rules
	 * @param requestFor
	 * @return
	 * added to get  type of input service request we receive
	 */
	public Rules fireRulesFor(Rules rules, String requestFor) throws DroolsParseException {
		
		logger.info("Firing Rules for " + requestFor);

		try {
			kieSession.insert(rules);
			kieSession.getAgenda().getAgendaGroup(requestFor).setFocus();
			kieSession.fireAllRules();
			kieSession.delete(kieSession.getFactHandle(rules));
		} catch (Exception e) {
			throw new DroolsParseException(e.getMessage());
		}

		return rules;
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
