package com.anthem.hca.smartpcp.affinity.repo;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.anthem.hca.smartpcp.affinity.constants.MemberConstants;
import com.anthem.hca.smartpcp.affinity.model.PcpIdWithRank;
import com.anthem.hca.smartpcp.affinity.rowmapper.PcpIdWithRankRowMapper;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.Member;

public class MemberRepoTest {

	@Mock
	MemberRepo memberRepo;
	@Mock
	private NamedParameterJdbcTemplate jdbcNamedTemplate;

	@Value("${affinity.schema}")
	private String schema;
	@Value("${affinity.memberTable}")
	private String memberTable;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getPCPIdWithRankListTest() throws ParseException{

		Member member = getMemberInfo();
		List<PcpIdWithRank> expectedPcpIdWithRankList = getpcpIdWithRankList();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(MemberConstants.MBR_SQNC_NBR, member.getMemberSequenceNumber().replaceFirst("^0+(?!$)", ""));
		parameters.addValue(MemberConstants.MBR_HCID, member.getMemberEid());
		parameters.addValue(MemberConstants.MBR_DOB, member.getMemberDob());
		parameters.addValue(MemberConstants.MBR_FIRST_NAME, member.getMemberFirstName());		
		String query ="SELECT DISTINCT "+MemberConstants.MCID_LABEL+" AS MCID, "+MemberConstants.TAX_ID_LABEL+" AS TIN, "+MemberConstants.IP_NPI_LABEL+" AS NPI, "+MemberConstants.PROV_PCP_ID_LABEL+" AS PCP_ID, "+MemberConstants.PIMS_RANK_LABEL+" AS PCP_RANK FROM "+schema+"."+memberTable+" WHERE "+MemberConstants.HC_ID_LABEL+" = :hcId AND "+MemberConstants.BRTH_DT_LABEL+" = :birthDt AND "+MemberConstants.FRST_NM_LABEL+" = :firstName AND "+MemberConstants.MBR_SQNC_NBR_LABEL+" = :memberSeqNo ORDER BY "+MemberConstants.PIMS_RANK_LABEL+" ASC";

		Mockito.when(memberRepo.getPCPIdWithRankList(member)).thenReturn(expectedPcpIdWithRankList);
		Mockito.when(jdbcNamedTemplate.query(query,parameters,new PcpIdWithRankRowMapper())).thenReturn(expectedPcpIdWithRankList);

		List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<PcpIdWithRank>();
		pcpIdWithRankList = (ArrayList<PcpIdWithRank>) memberRepo.getPCPIdWithRankList(member);

		assertEquals(pcpIdWithRankList, expectedPcpIdWithRankList);
	}

	private List<PcpIdWithRank> getpcpIdWithRankList() {

		List<PcpIdWithRank> pcpIdWithRankList = new ArrayList<PcpIdWithRank>();

		PcpIdWithRank pcpIdWithRankA = new PcpIdWithRank();
		pcpIdWithRankA.setMcid("369709015");
		pcpIdWithRankA.setPcpId("Z0Z017");
		pcpIdWithRankA.setTin("954337143"); 
		pcpIdWithRankA.setNpi("1750312294");
		pcpIdWithRankA.setPcpRank("2");
		pcpIdWithRankList.add(pcpIdWithRankA);

		PcpIdWithRank pcpIdWithRankB = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankB.setPcpId("Z0Z213");
		pcpIdWithRankB.setTin("954337143"); 
		pcpIdWithRankB.setNpi("1104935329");
		pcpIdWithRankB.setPcpRank("3");
		pcpIdWithRankList.add(pcpIdWithRankB);

		PcpIdWithRank pcpIdWithRankC = new PcpIdWithRank();
		pcpIdWithRankB.setMcid("369709015");
		pcpIdWithRankC.setPcpId("Z0Z030");
		pcpIdWithRankC.setTin("954337143"); 
		pcpIdWithRankC.setNpi("1699777896");
		pcpIdWithRankC.setPcpRank("4");
		pcpIdWithRankList.add(pcpIdWithRankC);

		return pcpIdWithRankList;
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