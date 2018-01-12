package com.deloitte.demo.DroolsXLSPrototype.drools;

import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.io.Serializable;

/**
 * Created by yuntliu on 1/6/2018.
 */
public class SessionApplier implements Serializable

{
    public String xlsFileName;
    public SessionApplier (String xlsFileName)
    {
        this.xlsFileName = xlsFileName;
    }
    public Product applyDiscountRule (Product product)  throws Exception {
        StatelessKnowledgeSession ksession = SessionBaseFactory.createKnowledgeBaseFromSpreadsheet(xlsFileName);
        ksession.execute(product);
        return product;
    }

}
