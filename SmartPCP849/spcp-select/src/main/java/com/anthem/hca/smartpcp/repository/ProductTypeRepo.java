package com.anthem.hca.smartpcp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Repository class to retrieve product type of member from PROD_PLAN and PROD_PKG tables.
 * 
 * 
 * @author AF71111
 */
@RefreshScope
@Repository
public class ProductTypeRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductTypeRepo.class);

	@Value("${product.type.prod.plan.qry}")
	private String producttypePlanQry;
	
	@Value("${product.type.prod.pkg.qry}")
	private String productTypePkgQry;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * @param product
	 * @param memberEffectiveDate
	 * @return String
	 */
	public Map<String,String> getProductTypePlan(String product, String memberEffectiveDate) {
		
		
		LOGGER.debug("Query to retrieve product type from PLAN table {}", producttypePlanQry);
		return jdbcTemplate.query(producttypePlanQry, new Object[] { product, memberEffectiveDate, memberEffectiveDate },
				new ResultSetExtractor<Map<String,String>>() {
					@Override
					public Map<String,String> extractData(ResultSet resultSet) throws SQLException {
						Map<String,String> attributes = new HashMap<>();
						while(resultSet.next()){
							attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, resultSet.getString(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL));
							attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, resultSet.getString(ProductPlanConstants.PLAN_ST_CD_LABEL));
						}
						return attributes;
					}

				});
	}

	/**
	 * @param product
	 * @return String
	 */
	@Transactional(readOnly = true)
	public String getProductTypeProd(String product) {
		
		LOGGER.debug("Query to retrieve product type from PROD table {}", productTypePkgQry);
		return jdbcTemplate.query(productTypePkgQry, new Object[] { product }, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet resultSet) throws SQLException {
				return resultSet.next() ? resultSet.getString(ProductPlanConstants.HLTH_PROD_SRVC_TYPE_CD_LABEL)
						: Constants.EMPTY_STRING;
			}

		});
	}
}
