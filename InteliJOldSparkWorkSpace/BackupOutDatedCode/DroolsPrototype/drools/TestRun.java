package com.deloitte.demo.DroolsPrototype.drools;

import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseFactory;
import org.drools.core.WorkingMemory;
import scala.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by yuntliu on 12/7/2017.
 */
public class TestRun {

    public static void main(String[] args ) throws DroolsParserException, IOException
    {

/*        TestRun tr = new TestRun();
        tr.executeDrools();*/

        DroolsRulesApplier rulesApplier = new DroolsRulesApplier("/rules/Rules.drl");
        String inputValue = "something";
        String outputValue = rulesApplier.applyRule(inputValue);
        System.out.println(outputValue);

        rulesApplier = new DroolsRulesApplier("/rules/DiscountRule.drl");
        Product product = new Product();
       product.setType("diamond");
        product.setDiscount(1);
        product = rulesApplier.applyDiscountRule(product);
        System.out.println(product.getDiscount());
        product.setType("gold");
        product.setDiscount(1);
        product = rulesApplier.applyDiscountRule(product);
        System.out.println(product.getDiscount());


    }

    public void executeDrools() throws DroolsParserException, IOException {

        PackageBuilder packageBuilder = new PackageBuilder();
        String ruleFile = "/rules/Rules.drl";
        InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);
        Reader reader = new InputStreamReader(resourceAsStream);
        packageBuilder.addPackageFromDrl(reader);
        org.drools.core.rule.Package rulesPackage = packageBuilder.getPackage();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage(rulesPackage);
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        String inputValue = "something";
        Message message = new Message(inputValue);

        workingMemory.insert(message);
        workingMemory.fireAllRules();

        System.out.println(message.getContent());

    }

}
