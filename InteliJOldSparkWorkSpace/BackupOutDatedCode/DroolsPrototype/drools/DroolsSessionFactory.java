package com.deloitte.demo.DroolsPrototype.drools;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.*;

/**
 * Created by yuntliu on 12/7/2017.
 */
public class DroolsSessionFactory implements Serializable{

    /**
     * Creates a new KieSession instance, configured with the rules from the given session.
     *
     * @param sessionName the session name from which rules should be retrieved
     * @return the instantiated KieSession
     */
    protected  WorkingMemory createDroolsSession(String sessionName) {
/*        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        return kieContainer.newKieSession(sessionName);*/
        WorkingMemory workingMemory = null;
try {

    PackageBuilder packageBuilder = new PackageBuilder();
    String ruleFile = sessionName;
    InputStream resourceAsStream = DroolsSessionFactory.class.getResourceAsStream(ruleFile);
    Reader reader = new InputStreamReader(resourceAsStream);
    packageBuilder.addPackageFromDrl(reader);
    org.drools.core.rule.Package rulesPackage = packageBuilder.getPackage();
    RuleBase ruleBase = RuleBaseFactory.newRuleBase();
    ruleBase.addPackage(rulesPackage);
    workingMemory = ruleBase.newStatefulSession();
}
        catch (IOException E)
        {

        }
        catch (DroolsParserException e)
        {
        }

    return workingMemory;


    }
}
