package com.anthem.hca.smartpcp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;

@Repository
public class GeocodeRepo {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeocodeRepo.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Map<String, Address> geocodeMap;

	public void getGeoCodes(Member member) {

		String geoCodeQuery = "SELECT LATITUDE,LONGITUDE FROM DV_PDPSPCP_XM.ZIPCODE_REPO WHERE ZIP_CD=?;";
		long startFetch = System.nanoTime();

		List<Double> list = jdbcTemplate.query(geoCodeQuery, new Object[] { member.getAddress().getZipCode() },
				new ResultSetExtractor<List<Double>>() {
					@Override
					public List<Double> extractData(ResultSet resultSet) throws SQLException {
						List<Double> attributes = new ArrayList<>();
						while (resultSet.next()) {
							attributes.add(resultSet.getDouble("LATITUDE"));
							attributes.add(resultSet.getDouble("LONGITUDE"));
						}
						return attributes;
					}

				});
		if (list != null && list.size() >= 2) {
			member.getAddress().setLatitude(list.get(0));
			member.getAddress().setLongitude(list.get(1));
		}
		long endFetch = System.nanoTime();

		LOGGER.info("time getGeoCodes {} milliseconds", (endFetch - startFetch) / 1000000);

	}

	@PostConstruct
	public void getGeoCodes() {

		String geoCodeQuery = "SELECT * FROM DV_PDPSPCP_XM.ZIPCODE_REPO";
		long startFetch = System.nanoTime();

		geocodeMap = jdbcTemplate.query(geoCodeQuery, new ResultSetExtractor<Map<String, Address>>() {
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

		long endFetch = System.nanoTime();

		LOGGER.info("time to form getGeoCodes {} milliseconds", (endFetch - startFetch) / 1000000);

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
