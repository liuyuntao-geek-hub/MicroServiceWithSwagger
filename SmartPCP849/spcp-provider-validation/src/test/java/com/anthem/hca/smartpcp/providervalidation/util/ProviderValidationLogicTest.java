package com.anthem.hca.smartpcp.providervalidation.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.anthem.hca.smartpcp.providervalidation.utility.ProviderValidationLogic;
import com.anthem.hca.smartpcp.providervalidation.vo.ActionPair;
import com.anthem.hca.smartpcp.providervalidation.vo.Member;
import com.anthem.hca.smartpcp.providervalidation.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.vo.Rules;

public class ProviderValidationLogicTest {

	private static ProviderValidationLogic providerValidationLogic;

	@BeforeClass
	public static void init() {
		providerValidationLogic = new ProviderValidationLogic();
	}

	@Test
	public void providerValidationlogicPositiveTest() {

		// Rule Details
		List<ActionPair<String, Boolean>> validationFlagList = new ArrayList<ActionPair<String, Boolean>>();
		ActionPair<String, Boolean> ageValidationMap = new ActionPair<String, Boolean>("AGE_RANGE_VALIDATION", true);
		ActionPair<String, Boolean> genderValidationMap = new ActionPair<String, Boolean>("GENDER_VALIDATION", true);
		ActionPair<String, Boolean> specialtyValidationMap = new ActionPair<String, Boolean>("SPECIALTY_VALIDATION",
				true);
		ActionPair<String, Boolean> contractValidationMap = new ActionPair<String, Boolean>("CONTRACT_VALIDATION",
				true);
		ActionPair<String, Boolean> distanceValidationMap = new ActionPair<String, Boolean>("DISTANCE_VALIDATION",
				true);
		ActionPair<String, Boolean> panelCapacityValidationMap = new ActionPair<String, Boolean>(
				"PANEL_CAPACITY_VALIDATION", true);
		ActionPair<String, Boolean> networkValidationMap = new ActionPair<String, Boolean>("NETWORK_VALIDATION", true);
		ActionPair<String, Boolean> tierValidationMap = new ActionPair<String, Boolean>("TIER_VALIDATION", true);
		ActionPair<String, Boolean> acceptPatientsValidationMap = new ActionPair<String, Boolean>(
				"ACCEPTING_PATIENTS_VALIDATION", true);
		ActionPair<String, Boolean> rolloverValidationMap = new ActionPair<String, Boolean>("ROLLOVER_VALIDATION",
				true);

		validationFlagList.add(ageValidationMap);
		validationFlagList.add(genderValidationMap);
		validationFlagList.add(specialtyValidationMap);
		validationFlagList.add(contractValidationMap);
		validationFlagList.add(distanceValidationMap);
		validationFlagList.add(panelCapacityValidationMap);
		validationFlagList.add(networkValidationMap);
		validationFlagList.add(tierValidationMap);
		validationFlagList.add(acceptPatientsValidationMap);
		validationFlagList.add(rolloverValidationMap);

		Rules pvRules = new Rules();
		pvRules.setValidationFlagList(validationFlagList);
		List<String> primarySpecialties = new ArrayList<String>();
		primarySpecialties.add("Pediatrics");
		primarySpecialties.add("Geriatrics");
		primarySpecialties.add("Internal Medicine");
		primarySpecialties.add("OB/GYN");
		pvRules.setPrimarySpecialties(primarySpecialties);
		pvRules.setRolloverFlag("N");

		List<ActionPair<String, Integer>> specialtyMaxAgeList = new ArrayList<>();
		ActionPair<String, Integer> pediatricsMaxAgeList = new ActionPair<String, Integer>("Pediatrics", 18);
		ActionPair<String, Integer> GeriatricsMaxAgeList = new ActionPair<String, Integer>("Geriatrics", 80);
		specialtyMaxAgeList.add(pediatricsMaxAgeList);
		specialtyMaxAgeList.add(GeriatricsMaxAgeList);

		List<ActionPair<String, Integer>> specialtyMinAgeList = new ArrayList<>();
		ActionPair<String, Integer> pediatricsMinAgeList = new ActionPair<String, Integer>("Pediatrics", 0);
		ActionPair<String, Integer> GeriatricsMinAgeList = new ActionPair<String, Integer>("Geriatrics", 60);
		specialtyMinAgeList.add(pediatricsMinAgeList);
		specialtyMinAgeList.add(GeriatricsMinAgeList);

		pvRules.setSpecialtyMaxAgeList(specialtyMaxAgeList);
		pvRules.setSpecialtyMinAgeList(specialtyMinAgeList);
		pvRules.setContractCushionPeriod(30);
		pvRules.setDistance(60);

		List<ActionPair<String, String>> specialtyGenderList = new ArrayList<>();
		ActionPair<String, String> OBGYNGenderList = new ActionPair<String, String>("OB/GYN", "F");
		specialtyGenderList.add(OBGYNGenderList);

		pvRules.setSpecialtyGenderList(specialtyGenderList);
		pvRules.setPanelCapacityPercent(85);

		String mdob = null;
		Date pEndDate = null;
		String mEffecDate = null;
		Date peffectiveDate=null;
		try {
			mdob = "2005-11-01";
			pEndDate = new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-31");
			peffectiveDate=new SimpleDateFormat("yyyy-MM-dd").parse("2014-10-01");
			mEffecDate = "2015-01-01";
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Provider Details
		PCP provider = new PCP();
		List<String> specialityList1 = new ArrayList<String>();
		specialityList1.add("Pediatrics");
		provider.setSpcltyDesc(specialityList1);
		provider.setDrivingDistance(30.0);
		provider.setProvPcpId("FAQ013");
		provider.setTierLvl(1);
		provider.setAccNewPatientFlag("Y");
		provider.setGrpgRltdPadrsTrmntnDt(pEndDate);
		provider.setGrpgRltdPadrsEfctvDt(peffectiveDate);
		provider.setCurntMbrCnt(11);
		provider.setMaxMbrCnt(111);
		provider.setRgnlNtwkId("CAHVV100");

		// Member Details
		Member mem = new Member();
		mem.setMemberDob(mdob);
		mem.setMemberGender("F");
		List<String> memNetId=new ArrayList<String>();
		memNetId.add("CAHVV100");
		memNetId.add("CAHVV");
		mem.setMemberNetworkId(memNetId);
		mem.setRollOverPcpId("FAQ013");
		mem.setMemberEffectiveDate(String.valueOf(mEffecDate));
		//mem.setInvocationSystem("08");
		

		// Provider Validations
		assertEquals(true, providerValidationLogic.isRollOver(mem, provider,pvRules));
		assertEquals(true, providerValidationLogic.isAcceptPatient(provider,pvRules));
		assertEquals(true, providerValidationLogic.isAgeValidate(mem, provider, pvRules));
		//assertEquals(true, providerValidationLogic.isGendrValidate(mem, provider, pvRules));
		assertEquals(true, providerValidationLogic.isSpecialtyValidate(provider, pvRules));
		assertEquals(true, providerValidationLogic.isNtwkValidate(mem, provider, pvRules));
		assertEquals(true, providerValidationLogic.isCntrctValidate(mem,provider, pvRules));
		assertEquals(true, providerValidationLogic.isDistanceValidate(provider, pvRules));
		assertEquals(true, providerValidationLogic.isPanelCpcityValidate(provider, pvRules));

	}

	@Test
	public void providerValidationlogicNegitiveTest() {

		// Rule Details
		List<ActionPair<String, Boolean>> validationFlagList = new ArrayList<ActionPair<String, Boolean>>();
		ActionPair<String, Boolean> ageValidationMap = new ActionPair<String, Boolean>("AGE_RANGE_VALIDATION", true);
		ActionPair<String, Boolean> genderValidationMap = new ActionPair<String, Boolean>("GENDER_VALIDATION", true);
		ActionPair<String, Boolean> specialtyValidationMap = new ActionPair<String, Boolean>("SPECIALTY_VALIDATION",
				true);
		ActionPair<String, Boolean> contractValidationMap = new ActionPair<String, Boolean>("CONTRACT_VALIDATION",
				true);
		ActionPair<String, Boolean> distanceValidationMap = new ActionPair<String, Boolean>("DISTANCE_VALIDATION",
				true);
		ActionPair<String, Boolean> panelCapacityValidationMap = new ActionPair<String, Boolean>(
				"PANEL_CAPACITY_VALIDATION", true);
		ActionPair<String, Boolean> networkValidationMap = new ActionPair<String, Boolean>("NETWORK_VALIDATION", true);
		ActionPair<String, Boolean> tierValidationMap = new ActionPair<String, Boolean>("TIER_VALIDATION", true);
		ActionPair<String, Boolean> acceptPatientsValidationMap = new ActionPair<String, Boolean>(
				"ACCEPTING_PATIENTS_VALIDATION", true);
		ActionPair<String, Boolean> rolloverValidationMap = new ActionPair<String, Boolean>("ROLLOVER_VALIDATION",
				true);

		validationFlagList.add(ageValidationMap);
		validationFlagList.add(genderValidationMap);
		validationFlagList.add(specialtyValidationMap);
		validationFlagList.add(contractValidationMap);
		validationFlagList.add(distanceValidationMap);
		validationFlagList.add(panelCapacityValidationMap);
		validationFlagList.add(networkValidationMap);
		validationFlagList.add(tierValidationMap);
		validationFlagList.add(acceptPatientsValidationMap);
		validationFlagList.add(rolloverValidationMap);

		Rules pvRulesNeg = new Rules();
		pvRulesNeg.setValidationFlagList(validationFlagList);
		List<String> primarySpecialties = new ArrayList<String>();
		primarySpecialties.add("Pediatrics");
		primarySpecialties.add("Internal Medicine");
		primarySpecialties.add("OB/GYN");
		pvRulesNeg.setPrimarySpecialties(primarySpecialties);

		List<ActionPair<String, Integer>> specialtyMaxAgeList = new ArrayList<>();
		ActionPair<String, Integer> pediatricsMaxAgeList = new ActionPair<String, Integer>("Pediatrics", 18);
		ActionPair<String, Integer> GeriatricsMaxAgeList = new ActionPair<String, Integer>("Geriatrics", 80);
		specialtyMaxAgeList.add(pediatricsMaxAgeList);
		specialtyMaxAgeList.add(GeriatricsMaxAgeList);

		List<ActionPair<String, Integer>> specialtyMinAgeList = new ArrayList<>();
		ActionPair<String, Integer> pediatricsMinAgeList = new ActionPair<String, Integer>("Pediatrics", 0);
		ActionPair<String, Integer> GeriatricsMinAgeList = new ActionPair<String, Integer>("Geriatrics", 60);
		specialtyMinAgeList.add(pediatricsMinAgeList);
		specialtyMinAgeList.add(GeriatricsMinAgeList);

		pvRulesNeg.setSpecialtyMaxAgeList(specialtyMaxAgeList);
		pvRulesNeg.setSpecialtyMinAgeList(specialtyMinAgeList);
		pvRulesNeg.setContractCushionPeriod(30);
		pvRulesNeg.setDistance(60);

		List<ActionPair<String, String>> specialtyGenderList = new ArrayList<>();
		ActionPair<String, String> OBGYNGenderList = new ActionPair<String, String>("OB/GYN", "F");
		specialtyGenderList.add(OBGYNGenderList);

		pvRulesNeg.setSpecialtyGenderList(specialtyGenderList);
		pvRulesNeg.setPanelCapacityPercent(85);

		Integer[] tiersLevel = { 1, 2, 3 };
		
		String mdob = null;
		Date pEndDate = null;
		String mEffecDate = null;
		try {
			mdob ="2005-11-01";
			pEndDate = new SimpleDateFormat("yyyy-MM-dd").parse("2018-05-15");
			mEffecDate = "2018-05-28";
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Provider Details
		PCP provider = new PCP();
		
		List<String> specialityList2 = new ArrayList<String>();
		specialityList2.add("Geriatrics");
		provider.setSpcltyDesc(specialityList2);
		provider.setDrivingDistance(80.0);
		provider.setProvPcpId("FAQ013");
		provider.setTierLvl(0);
		provider.setAccNewPatientFlag("N");
		provider.setGrpgRltdPadrsTrmntnDt(pEndDate);
		provider.setCurntMbrCnt(111);
		provider.setMaxMbrCnt(11);
		provider.setRgnlNtwkId("CAHVV100");
		

		// Member Details
		Member mem = new Member();
		mem.setMemberDob(String.valueOf(mdob));
		mem.setMemberGender("M");
		List<String> memNetId=new ArrayList<String>();
		memNetId.add("CAHVV");
		memNetId.add("CAHVV");
		mem.setMemberNetworkId(memNetId);
		mem.setRollOverPcpId("FAQ013");
		mem.setMemberEffectiveDate(String.valueOf(mEffecDate));
		//mem.setInvocationSystem("06");
		pvRulesNeg.setRolloverFlag("Y");
		// Provider Validations
		assertEquals(false, providerValidationLogic.isRollOver(mem, provider,pvRulesNeg));
		assertEquals(false, providerValidationLogic.isAcceptPatient(provider,pvRulesNeg));
		assertEquals(false, providerValidationLogic.isAgeValidate(mem, provider, pvRulesNeg));
		assertEquals(false, providerValidationLogic.isSpecialtyValidate(provider, pvRulesNeg));
		List<String> specialityList3 = new ArrayList<String>();
		specialityList3.add("OB/GYN");
		provider.setSpcltyDesc(specialityList3);
		//assertEquals(false, providerValidationLogic.isGendrValidate(mem, provider, pvRulesNeg));
		assertEquals(false, providerValidationLogic.isNtwkValidate(mem, provider, pvRulesNeg));
		assertEquals(false, providerValidationLogic.isCntrctValidate(mem,provider, pvRulesNeg));
		assertEquals(false, providerValidationLogic.isDistanceValidate(provider, pvRulesNeg));
		assertEquals(false, providerValidationLogic.isPanelCpcityValidate(provider, pvRulesNeg));

	}

}