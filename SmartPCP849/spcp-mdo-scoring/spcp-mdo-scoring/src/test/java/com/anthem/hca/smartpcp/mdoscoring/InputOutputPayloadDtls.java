/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.anthem.hca.smartpcp.mdoscoring.vo.ActionPair;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.Address;
import com.anthem.hca.smartpcp.mdoscoring.vo.OutputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class InputOutputPayloadDtls {

	InputPayloadInfo ipPayload = new InputPayloadInfo();
	Member memInfo = new Member();
	PCP pcpInfo = new PCP();
	Rules rulesInfo = new Rules();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	public String createPayload() throws JsonProcessingException, ParseException {
		List<String> langCode = Arrays.asList("ENG", "SPA");
		memInfo.setMemberLanguageCode(langCode);

		Address adrs = new Address();
		adrs.setLatitude(23.45);
		adrs.setLongitude(123.45);
		memInfo.setAddress(adrs);

		// Setting providers Payload
		pcpInfo.setProvPcpId("FFYY90");
		pcpInfo.setVbpFlag("Y");
		pcpInfo.setAerialDistance(10.5);
		List<String> wgsCode = Arrays.asList("ENG", "SPA");
		pcpInfo.setPcpLang(wgsCode);
		pcpInfo.setPcpLastNm("Hunt");
		pcpInfo.setLatdCordntNbr(23.33);
		pcpInfo.setLngtdCordntNbr(24.45);
		pcpInfo.setSpcltyDesc("Pediatrics");

		pcpInfo.setCurntMbrCnt(250);
		pcpInfo.setMaxMbrCnt(2500);
		Date dob = df.parse("2017-12-01");
		pcpInfo.setGrpgRltdPadrsEfctvDt(dob);
		pcpInfo.setPcpRankgId("2");

		// Setting Rules Object
		List<String> specialties = Arrays.asList("Pediatrics", "Geratrics");
		rulesInfo.setRestrictedAgeSpecialties(specialties);

		List<ActionPair<Boolean, Integer>> objList = new ArrayList<>();
		objList.add(new ActionPair<Boolean, Integer>(true, 10));
		objList.add(new ActionPair<Boolean, Integer>(false, 0));

		rulesInfo.setAgeSpecialtyMatchScoreList(objList);
		rulesInfo.setLanguageMatchScoreList(objList);
		rulesInfo.setVbaparticipationScoreList(objList);

		rulesInfo.setLimitedTime(365);
		rulesInfo.setLimitedTimeScore(15);
		rulesInfo.setPanelCapacityPercent(25);

		List<ActionPair<Integer, Integer>> obList = new ArrayList<>();
		obList.add(new ActionPair<Integer, Integer>(1, 10));
		obList.add(new ActionPair<Integer, Integer>(2, 20));
		obList.add(new ActionPair<Integer, Integer>(3, 30));
		obList.add(new ActionPair<Integer, Integer>(4, 40));
		obList.add(new ActionPair<Integer, Integer>(5, 50));
		obList.add(new ActionPair<Integer, Integer>(0, 0));

		rulesInfo.setMdorankScoreList(obList);

		List<ActionPair<String, Integer>> objeList = new ArrayList<>();

		objeList.add(new ActionPair<String, Integer>("0-5", 50));
		objeList.add(new ActionPair<String, Integer>("5-10", 40));
		objeList.add(new ActionPair<String, Integer>("10-15", 30));
		objeList.add(new ActionPair<String, Integer>("15-20", 20));
		objeList.add(new ActionPair<String, Integer>("20+", 0));
		rulesInfo.setProximityScoreList(objeList);

		List<PCP> pcp = new ArrayList<>();
		pcp.add(pcpInfo);

		ipPayload.setMember(memInfo);
		ipPayload.setPcp(pcp);
		ipPayload.setRules(rulesInfo);
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(ipPayload);
		System.out.println("hi" + requestJson);
		return requestJson;

	}

	public InputPayloadInfo createInputPayload() throws ParseException {
		List<String> langCode = Arrays.asList("ENG", "SPA");
		memInfo.setMemberLanguageCode(langCode);
		Address adrs = new Address();
		adrs.setLatitude(23.45);
		adrs.setLongitude(123.45);
		memInfo.setAddress(adrs);

		// Setting providers Payload
		pcpInfo.setProvPcpId("FFYY90");
		pcpInfo.setVbpFlag("Y");
		pcpInfo.setAerialDistance(10.5);
		List<String> wgsCode = Arrays.asList("ENG", "SPA");
		pcpInfo.setPcpLang(wgsCode);
		pcpInfo.setPcpLastNm("Hunt");
		pcpInfo.setLatdCordntNbr(23.33);
		pcpInfo.setLngtdCordntNbr(24.45);
		pcpInfo.setSpcltyDesc("Pediatrics");

		pcpInfo.setCurntMbrCnt(250);
		pcpInfo.setMaxMbrCnt(2500);
		Date dob = df.parse("2017-12-01");
		pcpInfo.setGrpgRltdPadrsEfctvDt(dob);
		pcpInfo.setPcpRankgId("2");

		// Setting Rules Object
		List<String> specialties = Arrays.asList("Pediatrics", "Geratrics");
		rulesInfo.setRestrictedAgeSpecialties(specialties);

		List<ActionPair<Boolean, Integer>> objList = new ArrayList<>();
		objList.add(new ActionPair<Boolean, Integer>(true, 10));
		objList.add(new ActionPair<Boolean, Integer>(false, 0));

		rulesInfo.setAgeSpecialtyMatchScoreList(objList);
		rulesInfo.setLanguageMatchScoreList(objList);
		rulesInfo.setVbaparticipationScoreList(objList);

		rulesInfo.setLimitedTime(365);
		rulesInfo.setLimitedTimeScore(15);
		rulesInfo.setPanelCapacityPercent(25);

		List<ActionPair<Integer, Integer>> obList = new ArrayList<>();
		obList.add(new ActionPair<Integer, Integer>(1, 10));
		obList.add(new ActionPair<Integer, Integer>(2, 20));
		obList.add(new ActionPair<Integer, Integer>(3, 30));
		obList.add(new ActionPair<Integer, Integer>(4, 40));
		obList.add(new ActionPair<Integer, Integer>(5, 50));
		obList.add(new ActionPair<Integer, Integer>(0, 0));

		rulesInfo.setMdorankScoreList(obList);

		List<ActionPair<String, Integer>> objeList = new ArrayList<>();

		objeList.add(new ActionPair<String, Integer>("0-5", 50));
		objeList.add(new ActionPair<String, Integer>("5-10", 40));
		objeList.add(new ActionPair<String, Integer>("10-15", 30));
		objeList.add(new ActionPair<String, Integer>("15-20", 20));
		objeList.add(new ActionPair<String, Integer>("20+", 0));
		rulesInfo.setProximityScoreList(objeList);

		List<PCP> pcp = new ArrayList<>();
		pcp.add(pcpInfo);

		ipPayload.setMember(memInfo);
		ipPayload.setPcp(pcp);
		ipPayload.setRules(rulesInfo);

		return ipPayload;
	}

	public OutputPayloadInfo createOutputPayload() throws ParseException {
		OutputPayloadInfo outputPayload = new OutputPayloadInfo();

		outputPayload.setProvPcpId("FFYY90");
		outputPayload.setDrivingDistance(12.8);
		outputPayload.setRgnlNtwkId("123");
		outputPayload.setMdoScore(95);
		outputPayload.setResponseCode("200");
		outputPayload.setResponseMessage("SUCCESS");
		return outputPayload;
	}
}
