package com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools;

/**
 * Created by yuntliu on 1/22/2018.
 */
import java.io.File;
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
    public File file;
    public KieSessionApplier (String xlsFileName)
    {
        this.xlsFileName = xlsFileName;
    }
    public KieSessionApplier (File resource)
    {
        this.file = resource;
    }
    public Product applyDiscountRule (Product product){
        StatelessKieSession ksession = KieSessionFactory.getKieSession(xlsFileName);
        ksession.execute(product);
        return product;
    }

    public Product applyDiscountRuleFromFile (Product product){
        StatelessKieSession ksession = KieSessionFactory.getKieSessionFromFile(file);
        ksession.execute(product);
        return product;
    }
}
