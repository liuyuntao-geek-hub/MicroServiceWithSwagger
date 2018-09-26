package com.anthem.hca.smartpcp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadExecutor {
	
	private int numberOfThreads = 4;
	
	private ExecutorService executorService;
	
	public ThreadExecutor(){
		setExecutorService(Executors.newFixedThreadPool(numberOfThreads));
	}

	/**
	 * @return the executorService
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * @param executorService the executorService to set
	 */
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	

}
