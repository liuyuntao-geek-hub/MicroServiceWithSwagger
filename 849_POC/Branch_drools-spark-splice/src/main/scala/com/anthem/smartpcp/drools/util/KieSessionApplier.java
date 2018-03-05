package com.anthem.smartpcp.drools.util;

import java.io.File;
import java.io.Serializable;
import org.kie.api.runtime.StatelessKieSession;

import com.anthem.smartpcp.drools.model.State;

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
    public State applyShortNameRule (State state){
        StatelessKieSession ksession = KieSessionFactory.getKieSession(xlsFileName);
        ksession.execute(state);
        return state;
    }

    public State applyShortnameRuleFromFile (State state){
        StatelessKieSession ksession = KieSessionFactory.getKieSessionFromFile(file);
        ksession.execute(state);
        return state;
    }
}
