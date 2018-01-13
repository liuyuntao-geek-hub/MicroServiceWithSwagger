package com.deloitte.demo.DroolsXLSPrototype.drools


import java.io.File;
import com.deloitte.demo.framework._;
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
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataInputStream;;


object KieSessionFactoryScala extends Serializable {
  
  var statelessKieSession:StatelessKieSession = null
     
  def  getKieSessionStreamObj(filename:String ):StatelessKieSession= {
        if (statelessKieSession == null)
            statelessKieSession = getNewKieSessionStreamObj(filename);
        return statelessKieSession;
    }
  
  
       def  getNewKieSessionStreamObj ( drlFileName:String):StatelessKieSession= {
        System.out.println("creating a new kie session");

        val kieServices:KieServices = KieServices.Factory.get();
        val  kieResources:KieResources = kieServices.getResources();

        
         val StreamRule = OperationSession.hdfs.open(new Path(drlFileName))



        val kieFileSystem:KieFileSystem = kieServices.newKieFileSystem();
         val kieRepository:KieRepository = kieServices.getRepository();

    //    File drlFile = new File(drlFileName)
        
       try { 
        val resource:Resource = kieResources.newFileSystemResource(StreamRule.readUTF());
        
        //newInputStreamResource(arg0)
        
        kieFileSystem.write(resource);

        val kb:KieBuilder = kieServices.newKieBuilder(kieFileSystem);

        kb.buildAll();

        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n"
                    + kb.getResults().toString());
        }
        
       }
       catch 
       {
         case e:IOException => println("Failure on kieService")
       }

        val kContainer:KieContainer  = kieServices.newKieContainer(kieRepository
                .getDefaultReleaseId());
        return kContainer.newStatelessKieSession();
    }
    
      
      
}

