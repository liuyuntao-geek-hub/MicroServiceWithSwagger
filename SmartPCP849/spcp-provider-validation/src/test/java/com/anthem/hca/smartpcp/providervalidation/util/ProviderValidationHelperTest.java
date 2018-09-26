package com.anthem.hca.smartpcp.providervalidation.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.providervalidation.helper.ProviderValidationHelper;
import com.anthem.hca.smartpcp.providervalidation.utility.ProviderValidationLogic;
import com.anthem.hca.smartpcp.providervalidation.vo.ActionPair;
import com.anthem.hca.smartpcp.providervalidation.vo.Member;
import com.anthem.hca.smartpcp.providervalidation.vo.PCP;
import com.anthem.hca.smartpcp.providervalidation.vo.Rules;

public class ProviderValidationHelperTest {

	@InjectMocks
	private ProviderValidationHelper providerHelper;

	@Mock
	ProviderValidationLogic providerLogic;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getPCPForAffinityMDOTest() {

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
		Date peffectiveDate = null;
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
		provider.setRgnlNtwkId("CAHVV100");
		provider.setGrpgRltdPadrsTrmntnDt(pEndDate);
		provider.setGrpgRltdPadrsEfctvDt(peffectiveDate);
		provider.setCurntMbrCnt(11);
		provider.setAccNewPatientFlag("Y");
		provider.setMaxMbrCnt(111);
		provider.setTierLvl(1);
		List<String> specialityList1 = new ArrayList<String>();
		specialityList1.add("Pediatrics");
		provider.setSpcltyDesc(specialityList1);
		provider.setDrivingDistance(30.0);
		provider.setProvPcpId("FAQ013");

		PCP provider1 = new PCP();
		provider1.setRgnlNtwkId("CAHVV100");
		provider1.setGrpgRltdPadrsTrmntnDt(pEndDate);
		provider1.setGrpgRltdPadrsEfctvDt(peffectiveDate);
		provider1.setCurntMbrCnt(22);
		provider1.setAccNewPatientFlag("Y");
		provider1.setMaxMbrCnt(222);
		provider1.setTierLvl(1);
		List<String> specialityList2 = new ArrayList<String>();
		specialityList2.add("Pediatrics");
		provider1.setSpcltyDesc(specialityList2);
		provider1.setDrivingDistance(50.0);
		provider1.setProvPcpId("FAQ014");

		PCP provider2 = new PCP();
		provider2.setRgnlNtwkId("CAHVV100");
		provider2.setGrpgRltdPadrsTrmntnDt(pEndDate);
		provider2.setGrpgRltdPadrsEfctvDt(peffectiveDate);
		provider2.setCurntMbrCnt(111);
		provider2.setAccNewPatientFlag("N");
		provider2.setMaxMbrCnt(11);
		provider2.setTierLvl(1);
		List<String> specialityList3 = new ArrayList<String>();
		specialityList3.add("Geriatrics");
		provider2.setSpcltyDesc(specialityList3);
		provider2.setDrivingDistance(80.0);
		provider2.setProvPcpId("FAQ015");

		List<PCP> pcpInfo = new ArrayList<>();
		pcpInfo.add(provider);
		pcpInfo.add(provider1);
		pcpInfo.add(provider2);

		List<String> networkDetails = new ArrayList<>();
		networkDetails.add("CAHVV100");
		networkDetails.add("CA100");
		// Member Details
		Member mem = new Member();
		mem.setMemberDob(String.valueOf(mdob));
		mem.setMemberGender("F");
		mem.setMemberNetworkId(networkDetails);
		//mem.setRequestType("FALSE");
		mem.setRollOverPcpId("FAQ013");
		mem.setMemberEffectiveDate(String.valueOf(mEffecDate));
		//mem.setInvocationSystem("08");

		PCP pcpAffinity = providerHelper.getPCPForAffinity(mem, pcpInfo, pvRules);

		assertEquals(pcpInfo.get(0).getProvPcpId(), pcpAffinity.getProvPcpId());
		assertNotEquals(pcpInfo.get(1).getProvPcpId(), pcpAffinity.getProvPcpId());

		List<PCP> pcpMDO = providerHelper.getPCPListMDO(mem, pcpInfo, pvRules);

		assertThat(pcpMDO.size(), is(2));
		assertEquals("FAQ013", pcpMDO.get(0).getProvPcpId());
		assertEquals("FAQ014", pcpMDO.get(1).getProvPcpId());

		List<PCP> pcpInfoNeg = new ArrayList<>();
		pcpInfoNeg.add(provider2);
		PCP pcpAffinityNeg = providerHelper.getPCPForAffinity(mem, pcpInfoNeg, pvRules);
		List<PCP> pcpMDONeg = providerHelper.getPCPListMDO(mem, pcpInfoNeg, pvRules);
		assertNull(pcpAffinityNeg);
		assertThat(pcpMDONeg.size(), is(0));

	}

}
