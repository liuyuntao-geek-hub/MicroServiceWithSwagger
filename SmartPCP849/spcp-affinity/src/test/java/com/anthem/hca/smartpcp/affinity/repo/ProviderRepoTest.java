package com.anthem.hca.smartpcp.affinity.repo;

import static org.junit.Assert.assertEquals;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.anthem.hca.smartpcp.affinity.constants.ProviderConstants;
import com.anthem.hca.smartpcp.affinity.rowmapper.PcpRowMapper;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class ProviderRepoTest {

	@Mock
	ProviderRepo providerRepo;
	@Mock
	private NamedParameterJdbcTemplate jdbcNamedTemplate;

	@Value("${affinity.schema}")
	private String schema;
	@Value("${affinity.providerTable}")
	private String providerTable;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getPCPInfoDtlsListTest() throws ParseException{

		Member member = getMemberInfo();
		Set<String> pcpIdSet = new HashSet<>();
		pcpIdSet.add("Z0Z017");
		pcpIdSet.add("Z0Z213");
		pcpIdSet.add("Z0Z030");

		List<PCP> expectedPcpList = getProviderInfoDtls();
		List<String> memberNtwrkId = member.getMemberNetworkId();
		List<String> memberContractCode = member.getMemberContractCode();

		Set<String> memberNtwrkIdAndContractCodeSet = new HashSet<>();
		if(memberContractCode!=null && !memberContractCode.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberContractCode);
		}
		if(memberNtwrkId!=null && !memberNtwrkId.isEmpty()){
			memberNtwrkIdAndContractCodeSet.addAll(memberNtwrkId);
		}

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(ProviderConstants.MBR_NTWRK_ID_LIST, memberNtwrkIdAndContractCodeSet);
		parameters.addValue(ProviderConstants.PCP_ID_LIST, pcpIdSet);

		String query = "SELECT "+ProviderConstants.PCP_ID_LABEL+" AS PROV_PCP_ID, " +ProviderConstants.LATD_LABEL+" AS LATD_CORDNT_NBR, " +ProviderConstants.LNGTD_LABEL+" AS LNGTD_CORDNT_NBR, " +ProviderConstants.RGNL_NTWK_ID_LABEL+" AS RGNL_NTWK_ID, " +ProviderConstants.TIER_LEVEL_LABEL+" AS TIER_LEVEL, "+ProviderConstants.SPCLTY_DESC_LABEL+"  AS SPCLTY_DESC, "+ProviderConstants.GRPG_RLTD_PADRS_TRMNTN_DT_LABEL+" AS  GRPG_RLTD_PADRS_TRMNTN_DT, "+ProviderConstants.GRPG_RLTD_PADRS_EFCTV_DT_LABEL+" AS GRPG_RLTD_PADRS_EFCTV_DT, "+ProviderConstants.MAX_MBR_CNT_LABEL+", "+ProviderConstants.CURNT_MBR_CNT_LABEL+", "+ProviderConstants.ACC_NEW_PATIENT_FLAG_LABEL+" FROM " +schema+"."+providerTable+" WHERE "+ProviderConstants.PCP_ID_LABEL+" IN (:pcpIdSet) AND "+ProviderConstants.RGNL_NTWK_ID_LABEL+" IN (:memberNtwrkIdList)";

		Mockito.when(providerRepo.getPCPInfoDtlsList(member,pcpIdSet)).thenReturn(expectedPcpList);
		Mockito.when(jdbcNamedTemplate.query(query,parameters,new PcpRowMapper())).thenReturn(expectedPcpList);

		List<PCP> PcpList = new ArrayList<PCP>();
		PcpList = (ArrayList<PCP>) providerRepo.getPCPInfoDtlsList(member,pcpIdSet);

		assertEquals(PcpList, expectedPcpList);
	}

	private List<PCP> getProviderInfoDtls() {

		List<PCP> pcpInfoDtlsList = new ArrayList<>();

		PCP pcpInfoDtlsA = new PCP();
		pcpInfoDtlsA.setProvPcpId("Z0Z017");
		pcpInfoDtlsA.setPcpRankgId("2");
		pcpInfoDtlsA.setLatdCordntNbr(35.408630);
		pcpInfoDtlsA.setLngtdCordntNbr(-119.031217);
		pcpInfoDtlsA.setSpcltyDesc("Family Practice");
		pcpInfoDtlsA.setRgnlNtwkId("CC0D");
		pcpInfoDtlsA.setGrpgRltdPadrsEfctvDt(new Date(2017-01-01));
		pcpInfoDtlsA.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsA.setMaxMbrCnt(2500);
		pcpInfoDtlsA.setCurntMbrCnt(0);
		pcpInfoDtlsA.setAccNewPatientFlag("Y");
		pcpInfoDtlsA.setAerialDistance(7.09391666666667);

		pcpInfoDtlsList.add(pcpInfoDtlsA);

		PCP pcpInfoDtlsB = new PCP();
		pcpInfoDtlsB.setProvPcpId("Z0Z213");
		pcpInfoDtlsB.setPcpRankgId("3");
		pcpInfoDtlsB.setLatdCordntNbr(35.769070);
		pcpInfoDtlsB.setLngtdCordntNbr(-119.245650);
		pcpInfoDtlsB.setSpcltyDesc("Pediatric");
		pcpInfoDtlsB.setRgnlNtwkId("CC0D");
		pcpInfoDtlsB.setGrpgRltdPadrsEfctvDt(new Date(2017-05-19));
		pcpInfoDtlsB.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsB.setMaxMbrCnt(2500);
		pcpInfoDtlsB.setCurntMbrCnt(0);
		pcpInfoDtlsB.setAccNewPatientFlag("Y");
		pcpInfoDtlsB.setAerialDistance(52.9071666666667);

		pcpInfoDtlsList.add(pcpInfoDtlsB);

		PCP pcpInfoDtlsC = new PCP();
		pcpInfoDtlsC.setProvPcpId("Z0Z030");
		pcpInfoDtlsC.setPcpRankgId("4");
		pcpInfoDtlsC.setLatdCordntNbr(35.399724);
		pcpInfoDtlsC.setLngtdCordntNbr(-119.465989);
		pcpInfoDtlsC.setSpcltyDesc("Family Practice");
		pcpInfoDtlsC.setRgnlNtwkId("CC0D");
		pcpInfoDtlsC.setGrpgRltdPadrsEfctvDt(new Date(2017-01-01));
		pcpInfoDtlsC.setGrpgRltdPadrsTrmntnDt(new Date(9999-12-31));
		pcpInfoDtlsC.setMaxMbrCnt(2500);
		pcpInfoDtlsC.setCurntMbrCnt(0);
		pcpInfoDtlsC.setAccNewPatientFlag("Y");
		pcpInfoDtlsC.setAerialDistance(44.8255833333333);

		pcpInfoDtlsList.add(pcpInfoDtlsC);

		return pcpInfoDtlsList;
	}

	private Member getMemberInfo() {

		Member member = new Member();
		member.setInvocationSystem("06");
		member.setSystemType("B");
		member.setMemberEid("976A78568");
		member.setMemberType("N");
		member.setMemberLineOfBusiness("CT1");
		member.setMemberProcessingState("CT");
		member.setMemberContractCode(Arrays.asList("CC86"));
		member.setMemberNetworkId(Arrays.asList("CC0D"));
		Address memberAddress = new Address();
		memberAddress.setLatitude(35.36645);
		memberAddress.setLongitude(-119.0098);
		member.setAddress(memberAddress);
		member.setMemberDob("1972-09-05");
		member.setMemberGender("M");
		member.setMemberSequenceNumber("2");
		member.setMemberFirstName("KERI");
		member.setRollOverPcpId("Z0Z07");
		member.setMemberEffectiveDate("1992-06-13");
		member.setMemberProductType("HMO");

		return member;
	}
}