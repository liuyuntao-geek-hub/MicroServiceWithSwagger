package com.anthem.hca.smartpcp.mdo.pool.config;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.DataSourceHealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

/** 
 * 
 * Copyright © 2018 Anthem, Inc.
 * 
 * 			ActuatorCustomConfig is a custom file designed to verify if the database connection can be established.
 * 
 * @author AF77390 
 */

@Configuration
@EnableRetry
public class ActuatorCustomConfig{

	private static final Logger logger = LoggerFactory.getLogger(ActuatorCustomConfig.class);

	@Value("${spring.datasource.username}")private String username;
	@Value("${spring.datasource.password}")private String password;
	@Value("${spring.datasource.driver-class-name}")private String driverclassname;
	@Value("${spring.datasource.url}")private String url;

	@Value("#{new Integer('${spring.datasource.tomcat.max-active}')}")private Integer maxActive;
	@Value("#{new Integer('${spring.datasource.tomcat.max-idle}')}")private Integer maxIdle;
	@Value("#{new Boolean('${spring.datasource.tomcat.test-on-borrow}')}")private Boolean testOnBorrow;
	@Value("#{new Integer('${spring.datasource.tomcat.min-idle}')}")private Integer minIdle;
	@Value("#{new Integer('${spring.datasource.tomcat.initial-size}')}")private Integer initialSize;
	@Value("#{new Integer('${spring.datasource.tomcat.min-evictable-idle-time-millis}')}")private Integer minEvictableIdleTimeMillis;
	@Value("#{new Integer('${spring.datasource.tomcat.max-age}')}")private Integer maxAge;
	@Value("#{new Integer('${spring.datasource.tomcat.time-between-eviction-runs-millis}')}")private Integer timeBetweenEvictionRunsMillis;
	@Value("#{new Integer('${spring.datasource.tomcat.validation-query-timeout}')}")private Integer validationQueryTimeout;
	@Value("${spring.datasource.tomcat.validation-query}")private String validationQuery;
	@Value("#{new Boolean('${spring.datasource.tomcat.remove-abandoned}')}")private Boolean removeAbandoned;
	@Value("#{new Integer('${spring.datasource.tomcat.remove-abandoned-timeout}')}")private Integer removeAbandonedTimeout;
	@Value("#{new Boolean('${spring.datasource.tomcat.log-abandoned}')}")private Boolean logAbandoned;
	@Value("#{new Integer('${spring.datasource.tomcat.abandon-when-percentage-full}')}")private Integer percentage;
	@Value("#{new Integer('${spring.datasource.tomcat.default-transaction-isolation}')}")private Integer defaultTransactionIsolation;
	@Value("#{new Integer('${spring.datasource.tomcat.validation-interval}')}")private Integer validationInterval;
	
	@Autowired(required=false)
	DataSourceBuilder dataSourceBuilder;//Autowired dataSourceBuilder so that one Object will be used on retry attempts

	/**
	 * method for datasource creation to verify if database connection can be established
	 * For any failure in connection establishment an attempt to retry for connection will be done in time intervals of (5,10,20,40s seconds)
	 * @return datasource object
	 * @throws SQLException
	 */
	@Bean
	@Retryable(value=Exception.class,maxAttempts=4,backoff=@Backoff(delay=5000,multiplier=2))
	public DataSource dataSource() throws SQLException{
		try{
			org.apache.tomcat.jdbc.pool.DataSource dataSource = null;
			dataSourceBuilder = DataSourceBuilder.create();
			dataSourceBuilder.url(url);
			dataSourceBuilder.driverClassName(driverclassname);
			dataSourceBuilder.username(username);
			dataSourceBuilder.password(password);
			dataSourceBuilder.build().getConnection();
			dataSource = (org.apache.tomcat.jdbc.pool.DataSource) dataSourceBuilder.build();

			dataSource.setMaxActive(maxActive);
			dataSource.setMaxIdle(maxIdle);
			dataSource.setTestOnBorrow(testOnBorrow);
			dataSource.setMinIdle(minIdle);
			dataSource.setInitialSize(initialSize);
			dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
			dataSource.setMaxAge(maxAge);
			dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
			dataSource.setValidationQueryTimeout(validationQueryTimeout);
			dataSource.setValidationQuery(validationQuery);
			dataSource.setRemoveAbandoned(removeAbandoned);
			dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
			dataSource.setLogAbandoned(logAbandoned);
			dataSource.setAbandonWhenPercentageFull(percentage);
			dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
			dataSource.setValidationInterval(validationInterval);
			return dataSource;
		}catch(Exception e){
			logger.error("Exception in ActuatorCustomConfig dataSource() method {}", e.getMessage(), e);
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * method to execute the custom query instead of default(SELECT 1) for splice machine database health status to show UP
	 * @return DataSourceHealthIndicator object after setting the query
	 * @throws SQLException
	 */
	@Bean
	public HealthIndicator dbHealthIndicator() {
		DataSourceHealthIndicator indicator = null;
		try{
			DataSource dataSource = dataSource();
			indicator = new DataSourceHealthIndicator(dataSource);
			indicator.setQuery(validationQuery);

		}catch(Exception e){
			logger.error("Exception in ActuatorCustomConfig dbHealthIndicator() method {}", e.getMessage(), e);
		}
		return indicator;
	}

}

