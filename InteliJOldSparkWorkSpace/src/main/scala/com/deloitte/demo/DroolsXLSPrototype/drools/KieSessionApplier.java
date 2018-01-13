package com.deloitte.demo.DroolsXLSPrototype.drools;

import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;;
/**
 * Created by yuntliu on 1/6/2018.
 */
public class KieSessionApplier implements Serializable {


    public String xlsFileName;
    public FSDataInputStream StreamRules;
    public KieSessionApplier (String xlsFileName)
    {
        this.xlsFileName = xlsFileName;
    }
    public KieSessionApplier (FSDataInputStream xlsFileName)
    {
        this.StreamRules = xlsFileName;
    }
    public Product applyDiscountRule (Product product){
        StatelessKieSession ksession = KieSessionFactory.getKieSession(xlsFileName);
        ksession.execute(product);
        return product;
    }

    public Product applyDiscountStreamRule (Product product){
        StatelessKieSession ksession = KieSessionFactory.getKieSessionStream(StreamRules);
        ksession.execute(product);
        return product;
    }
    
    public Product applyDiscountStreamRuleObj (Product product){
        StatelessKieSession ksession = KieSessionFactoryScala.getKieSessionStreamObj(xlsFileName);
        ksession.execute(product);
        return product;
    }
    
}
