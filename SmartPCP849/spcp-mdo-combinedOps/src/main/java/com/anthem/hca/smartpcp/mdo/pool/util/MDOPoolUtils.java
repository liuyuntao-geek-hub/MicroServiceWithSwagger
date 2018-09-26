/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class MDOPoolUtils {

	private static final Logger logger = LoggerFactory.getLogger(MDOPoolUtils.class);

	/**
	 * @param member
	 * @param geocodePool
	 * @param maxRadiusToPool
	 * @param poolSize
	 * @return List<PCP>
	 * 
	 *         Calculate the aerial distance between member and PCPs then sort
	 *         them based on the aerial distance between them
	 */
	public List<PCP> findArealDistance(Member member, List<PCP> pcpList, int maxRadiusToPool) {

		logger.info("In MdoPoolUtils for Calculating aerial distance between member and Providers {}", "");
		double memberLongitude = member.getAddress().getLongitude();
		double meberLatitude = member.getAddress().getLatitude();
		List<PCP> withinRadiusPcpPool = new ArrayList<>();
		double pcpLon;
		double pcpLat;
		double diffLongitude;
		double distance;
		try {
			if (null != pcpList && !pcpList.isEmpty()) {
				for (PCP pcpObject : pcpList) {

					if (null != pcpObject) {
						pcpLon = pcpObject.getLngtdCordntNbr();
						pcpLat = pcpObject.getLatdCordntNbr();
						diffLongitude = memberLongitude - pcpLon;
						distance = Math.sin(Math.toRadians(meberLatitude)) * Math.sin(Math.toRadians(pcpLat))
								+ Math.cos(Math.toRadians(meberLatitude)) * Math.cos(Math.toRadians(pcpLat))
										* Math.cos(Math.toRadians(diffLongitude));
						distance = Math.acos(distance);
						distance = Math.toDegrees(distance);
						distance = distance * 60 * 1.1515;

						if (distance <= maxRadiusToPool) {
							pcpObject.setAerialDistance(distance);
							withinRadiusPcpPool.add(pcpObject);
						}
					}
				}
				Collections.sort(withinRadiusPcpPool, new PCPComparatorSort());
				logger.info(
						"Providers are sorted based on the aerial distance, toltal providers with in aerial distance are {}",
						withinRadiusPcpPool.size());
			}

		} catch (Exception e) {
			logger.error("error while calculating aerial distance {} ", e);
		}

		return withinRadiusPcpPool;
	}

	public boolean isAgeUnder18(String dob) {

		boolean flag = true;
		LocalDate currentDate = LocalDate.now();
		Period p = Period.between(LocalDate.parse(dob), currentDate);
		if (p.getYears() > 18)
			flag = false;
		return flag;
	}
}
