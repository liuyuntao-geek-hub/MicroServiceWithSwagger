package com.deloitte.demo.DroolsXLSPrototype.drools;

import java.io.Serializable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Created by yuntliu on 1/6/2018.
 */
public class KieSessionApplier implements Serializable {


    public String xlsFileName;
    public KieSessionApplier (String xlsFileName)
    {
        this.xlsFileName = xlsFileName;
    }
    public Product applyDiscountRule (Product product){
        StatelessKieSession ksession = KieSessionFactory.getKieSession(xlsFileName);
        ksession.execute(product);
        return product;
    }

}
