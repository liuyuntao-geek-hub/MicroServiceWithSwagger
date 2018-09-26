/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.anthem.hca.smartpcp.mdoscoring.vo.ActionPair;
import com.anthem.hca.smartpcp.mdoscoring.vo.InputPayloadInfo;
import com.anthem.hca.smartpcp.mdoscoring.vo.Member;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.Rules;

public class InputPayloadTest {
	              
	InputPayloadInfo ipPayload = new InputPayloadInfo();
	Member memInfo = new Member();
	PCP pcpInfo=new PCP();
	Rules rulesInfo=new Rules();
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
/**
 * Data validation for Member Object
 * 
 */
	@Test
	public void getMemberRequestTest() throws Exception {
		memInfo.setMemberLanguageCode(Arrays.asList("01","02"));
		assertEquals("02",memInfo.getMemberLanguageCode().get(1));
	}
	/**
	 * Data validation for Provider Object
	 * 
	 */	
	@Test
	public void getProviderPCPRequestTest() throws Exception {
		pcpInfo.setProvPcpId("FFYY90");
		assertEquals("FFYY90",pcpInfo.getProvPcpId());
	}
	
	@Test
	public void getProviderSpecRequestTest() throws Exception {
        pcpInfo.setSpcltyDesc("Pediatrics");
        assertEquals("Pediatrics",pcpInfo.getSpcltyDesc());
	}
	
	@Test
	public void getProvidervbpRequestTest() throws Exception {
        pcpInfo.setVbpFlag("Y");
        assertEquals("Y",pcpInfo.getVbpFlag());   
	}
	@Test
	public void getProviderwgsRequestTest() throws Exception {
        List<String> wgsCode=Arrays.asList("01","02");
        pcpInfo.setPcpLang(wgsCode);
        assertEquals(wgsCode,pcpInfo.getPcpLang());
	} 
	@Test
	public void getProviderPCPntwRequestTest() throws Exception {
        pcpInfo.setCurntMbrCnt(250);
        pcpInfo.setMaxMbrCnt(2500);
        Date dob=df.parse("2017-12-01") ;
        pcpInfo.setGrpgRltdPadrsEfctvDt(dob);
		assertEquals(dob,pcpInfo.getGrpgRltdPadrsEfctvDt());
	}
	
	/**
	 * Data validation for Rules Object
	 * 
	 */
	@Test
	public void getRulesSpecRequestTest() throws Exception {
     List<String> specialties=Arrays.asList("Pediatrics","Geratrics");
     rulesInfo.setRestrictedAgeSpecialties(specialties);
     assertTrue(specialties.equals(rulesInfo.getRestrictedAgeSpecialties()));
	}
     @Test
 	public void getRulesAgeRequestTest() throws Exception {
     List<ActionPair<Boolean, Integer>> objList=new ArrayList<>();
     objList.add(new ActionPair<Boolean, Integer>(true, 10));
     objList.add(new ActionPair<Boolean, Integer>(false, 0));
     
     rulesInfo.setAgeSpecialtyMatchScoreList(objList);
     assertEquals(objList,rulesInfo.getAgeSpecialtyMatchScoreList());
 	}
     @Test
 	public void getRulesLangRequestTest() throws Exception { 
     List<ActionPair<Boolean, Integer>> objList=new ArrayList<>();
     objList.add(new ActionPair<Boolean, Integer>(true, 10));
     objList.add(new ActionPair<Boolean, Integer>(false, 0));
     rulesInfo.setLanguageMatchScoreList(objList);
     assertEquals(objList,rulesInfo.getLanguageMatchScoreList());
  	}
     @Test
 	public void getRulesvbaRequestTest() throws Exception {
     List<ActionPair<Boolean, Integer>> objList=new ArrayList<>();
     objList.add(new ActionPair<Boolean, Integer>(true, 10));
     objList.add(new ActionPair<Boolean, Integer>(false, 0));	 
     rulesInfo.setVbaparticipationScoreList(objList);
     assertEquals(objList,rulesInfo.getVbaparticipationScoreList());
  	}
    
     @Test
 	public void getRulesMdoRequestTest() throws Exception { 
     List<ActionPair<Integer, Integer>> obList=new ArrayList<>();
     obList.add(new ActionPair<Integer, Integer>(1, 10));
     obList.add(new ActionPair<Integer, Integer>(2, 20));
     obList.add(new ActionPair<Integer, Integer>(3, 30));
     obList.add(new ActionPair<Integer, Integer>(4, 40));
     obList.add(new ActionPair<Integer, Integer>(5, 50));
     obList.add(new ActionPair<Integer, Integer>(0, 0));
     
     rulesInfo.setMdorankScoreList(obList);
     assertEquals(obList,rulesInfo.getMdorankScoreList());
  	}
     
     @Test
 	public void getRulesproxRequestTest() throws Exception {  
     List<ActionPair<String, Integer>> objeList=new ArrayList<>();
   
     objeList.add(new ActionPair<String, Integer>("0-5",50));
     objeList.add(new ActionPair<String, Integer>("5-10",40));
     objeList.add(new ActionPair<String, Integer>("10-15",30));
     objeList.add(new ActionPair<String, Integer>("15-20",20));
     objeList.add(new ActionPair<String, Integer>("20+",0));
     rulesInfo.setProximityScoreList(objeList);
     assertEquals(objeList,rulesInfo.getProximityScoreList());
  	}
}
