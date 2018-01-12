package com.deloitte.demo.DroolsPrototype.drools;

import org.drools.core.WorkingMemory;
import org.kie.api.runtime.KieSession;

import java.io.Serializable;

/**
 * Created by yuntliu on 12/7/2017.
 */
public class DroolsRulesApplier implements Serializable {
    private  WorkingMemory KIE_SESSION;

    public DroolsRulesApplier(String sessionName) {
        KIE_SESSION = (new DroolsSessionFactory()).createDroolsSession(sessionName);
    }

    /**
     * Applies the loaded Drools rules to a given String.
     *
     * @param value the String to which the rules should be applied
     * @return the String after the rule has been applied
     */
    public String applyRule(String value) {
        Message message = new Message(value);
        KIE_SESSION.toString();
        KIE_SESSION.insert(message);
        KIE_SESSION.fireAllRules();
        return message.getContent();
    }

    public Product applyDiscountRule (Product product){

        KIE_SESSION.insert(product);
        KIE_SESSION.fireAllRules();
        return product;
    }

}
