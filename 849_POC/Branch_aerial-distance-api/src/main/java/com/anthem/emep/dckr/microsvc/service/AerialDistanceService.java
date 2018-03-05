package com.anthem.emep.dckr.microsvc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anthem.emep.dckr.microsvc.model.Geocodes;
import com.anthem.emep.dckr.microsvc.repo.IAerialDistanceRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AerialDistanceService {

	@Autowired
	public IAerialDistanceRepo repo;

	@Autowired
	public RestTemplate restTemplate;

	@Autowired
	public ObjectMapper objectMapper;
	
	 @Value("${bing.api}")
	 private String bingApi;

	public String distance(double lat, double lon) {

		List<Geocodes> providers = repo.findNearbyPoints(lon, lat);

		if (null != providers && providers.size() > 0) {

			return getDrivingDistance(lat, lon, providers);
		}
		return null;
	}

	private String getDrivingDistance(double lat, double lon, List<Geocodes> result) {

		try {
			String origins = lat + "," + lon;
			StringBuffer destination = new StringBuffer();

			for (int i = 0; i < result.size(); i++) {
				if (i == (result.size() - 1)) {
					destination.append(result.get(i).getLat() + "," + result.get(i).getLon());
				} else {
					destination.append(result.get(i).getLat() + "," + result.get(i).getLon() + ";");
				}
			}
			String url = bingApi + origins + "&destination=" + destination.toString();
			ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
			return res.getBody();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	public String distanceJava(double lat, double lon) {

		List<Geocodes> providers = repo.findAll();
		if (null != providers && providers.size() > 0) {
			List<Geocodes> withinRadius = new ArrayList<Geocodes>();
			for (int i = 0; i < providers.size(); i++) {
				Geocodes dest = providers.get(i);
				double theta = lon - dest.getLon();
				double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(dest.getLat()))
						+ Math.cos(deg2rad(lat)) * Math.cos(deg2rad(dest.getLat())) * Math.cos(deg2rad(theta));
				dist = Math.acos(dist);
				dist = rad2deg(dist);
				dist = dist * 60 * 1.1515;
				if (dist <= 60) {
					withinRadius.add(dest);
				}
			}
			if (null != withinRadius && withinRadius.size() > 0) {
				return getDrivingDistance(lat, lon, withinRadius);
			}
		}
		return null;
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
}
