package com.anthem.hca.smartpcp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.anthem.hca.smartpcp.model.Address;

@Repository
@RefreshScope
public class GeocodeRepo {


	@Value("${geocode.query}")
	private String geocodeQuery;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Map<String, Address> geocodeMap;


	@PostConstruct
	public void getGeoCodes() {

		geocodeMap = jdbcTemplate.query(geocodeQuery, new ResultSetExtractor<Map<String, Address>>() {
			@Override
			public Map<String, Address> extractData(ResultSet resultSet) throws SQLException {
				Map<String, Address> geocodes = new HashMap<>();
				while (resultSet.next()) {
					Address add = new Address();
					String zipcode = resultSet.getString("ZIP_CD");
					add.setZipCode(zipcode);
					add.setLatitude(resultSet.getDouble("LATITUDE"));
					add.setLongitude(resultSet.getDouble("LONGITUDE"));
					geocodes.put(zipcode, add);
				}
				return geocodes;
			}

		});
	}

	/**
	 * @return the geocodeMap
	 */
	public Map<String, Address> getGeocodeMap() {
		return geocodeMap;
	}

	/**
	 * @param geocodeMap
	 *            the geocodeMap to set
	 */
	public void setGeocodeMap(Map<String, Address> geocodeMap) {
		this.geocodeMap = geocodeMap;
	}

}
