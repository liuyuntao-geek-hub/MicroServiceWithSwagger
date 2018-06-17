package com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools;

/**
 * Created by yuntliu on 1/22/2018.
 */
public class TestRun {

    public static void main(String[] args ) throws Exception //throws DroolsParserException, IOException
    {

/*        TestRun tr = new TestRun();
        tr.executeDrools();*/

    
        KieSessionApplier rulesApplier = new KieSessionApplier("C:\\java\\git\\repos\\coe_2.0\\conf\\rules\\DroolsRuleDecisionTableDiscount.xls");
        // KieSessionApplier rulesApplier = new KieSessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\Rules.drl");
        // SessionApplier rulesApplier = new SessionApplier("C:\\Users\\yuntliu\\Documents\\workstation\\Study\\IntelliJ\\InteliJOldSparkWorkSpace\\src\\main\\resources\\rules\\DroolsRuleDecisionTableDiscount.xls");
        //  SessionApplier rulesApplier = new SessionApplier("\\rules\\DroolsRuleDecisionTableDiscount.xls");
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
}
