package com.anthem.hca.smartpcp.drools.util;

import org.kie.api.runtime.KieSession;

public class GlobalKieSession {
	
	private static GlobalKieSession globalKieSession;
	private KieSession kieSession;
	
	private GlobalKieSession() {}
	
	public KieSession getKieSession() {
		return kieSession;
	}

	public void setKieSession(KieSession kieSession) {
		this.kieSession = kieSession;
	}

	public static GlobalKieSession getInstance() {
        if (globalKieSession == null) {
        	globalKieSession = new GlobalKieSession();
        }
        return globalKieSession;
    }
}
