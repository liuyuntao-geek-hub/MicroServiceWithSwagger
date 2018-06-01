package com.anthem.hpip.smartPCPSelection.DroolsXLSPrototype.drools;

/**
 * Created by yuntliu on 1/22/2018.
 */

import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.io.Serializable;

/**
 * Created by yuntliu on 1/6/2018.
 */
public class SessionBaseFactory implements Serializable {

    public static StatelessKnowledgeSession session;
    public static StatelessKnowledgeSession createKnowledgeBaseFromSpreadsheet(String fileInPath)
            throws Exception {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory
                .newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory
                        .newClassPathResource(fileInPath),
                ResourceType.DTABLE, dtconf);

        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException(knowledgeBuilder.getErrors().toString());
        }

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages(knowledgeBuilder
                .getKnowledgePackages());
        session = knowledgeBase.newStatelessKnowledgeSession();
        return session;
    }
}