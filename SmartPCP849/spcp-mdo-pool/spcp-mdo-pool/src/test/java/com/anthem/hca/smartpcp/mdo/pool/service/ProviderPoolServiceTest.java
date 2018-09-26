/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import static org.junit.Assert.assertEquals;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.Address;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.model.ProviderPoolOutputPayload;
import com.anthem.hca.smartpcp.mdo.pool.repository.ProviderInfoRepo;
import com.anthem.hca.smartpcp.mdo.pool.util.MDOPoolUtils;

public class ProviderPoolServiceTest {

	@InjectMocks
	private ProviderPoolService providerPoolService;

	@Mock
	private MDOPoolUtils mdoPoolUtils;

	@Mock
	private DBConnectivityService spliceService;

	@Mock
	private ProviderInfoRepo providerInfoRepo;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void poolBuilderTest() throws SQLException {

		Member member = newMember();

		List<PCP> poolList = createPCPList();

		Mockito.when(providerPoolService.poolBuilder(2, member)).thenCallRealMethod();

		Mockito.doReturn(poolList).when(spliceService).getPCPDetails(Mockito.anyListOf(String.class),Mockito.any(Member.class));

		ProviderPoolOutputPayload providerPoolPayload = providerPoolService.poolBuilder(10, member);

		assertEquals(0, providerPoolPayload.getPcps().size());

	}

	@Test
	public void pcpValidationTest() throws SQLException, ParseException {

		Member member = newMember();
		List<PCP> poolList = createPCPList();
		List<PCP> validPCPList = validPCPList();

		Mockito.when(providerPoolService.poolBuilder(10, member)).thenCallRealMethod();

		Mockito.doReturn(poolList).when(spliceService).getPCPDetails(Mockito.anyListOf(String.class),Mockito.any(Member.class));
		Mockito.doReturn(validPCPList).when(mdoPoolUtils).findAerialDistance(Mockito.any(Member.class),
				Mockito.anyListOf(PCP.class), Mockito.anyInt());

		ProviderPoolOutputPayload providerPoolPayload = providerPoolService.poolBuilder(10, member);
		assertEquals(1, providerPoolPayload.getPcps().size());
	}

	@Test
	public void pcpValidationNegativeTest() throws SQLException, ParseException {

		Member member = newMember2();
		List<PCP> poolList = createPCPList();
		List<PCP> validPCPNegativeList = validPCPNegativeList();

		Mockito.when(providerPoolService.poolBuilder(10, member)).thenCallRealMethod();

		Mockito.doReturn(poolList).when(spliceService).getPCPDetails(Mockito.anyListOf(String.class),Mockito.any(Member.class));
		Mockito.doReturn(validPCPNegativeList).when(mdoPoolUtils).findAerialDistance(Mockito.any(Member.class),
				Mockito.anyListOf(PCP.class), Mockito.anyInt());

		ProviderPoolOutputPayload providerPoolPayload = providerPoolService.poolBuilder(10, member);
		assertEquals(0, providerPoolPayload.getPcps().size());
	}

	private List<PCP> createPCPList() {

		List<PCP> pcpList = new ArrayList<>();
		PCP pcp1 = new PCP();
		PCP pcp2 = new PCP();

		pcp1.setProvPcpId("AD1234");
		pcp1.setLatdCordntNbr(11.1234);
		pcp1.setLngtdCordntNbr(23.1234);
		pcp2.setProvPcpId("AZ4456");
		pcp2.setLatdCordntNbr(21.1234);
		pcp2.setLngtdCordntNbr(32.1234);
		pcpList.add(pcp1);
		pcpList.add(pcp2);
		return pcpList;
	}

	private List<PCP> validPCPList() throws ParseException {

		List<PCP> pcpList = new ArrayList<>();
		List<String> wgsLandCd = new ArrayList<>();
		PCP pcp1 = new PCP();
		PCP pcp2 = new PCP();

		wgsLandCd.add("ENG");
		pcp1.setProvPcpId("AD1234");
		pcp1.setLatdCordntNbr(11.1234);
		pcp1.setLngtdCordntNbr(23.1234);
		pcp1.setSpcltyDesc("Internal Medicine");
		pcp1.setPcpLang(wgsLandCd);
		pcp1.setMaxMbrCnt(123);
		pcp1.setCurntMbrCnt(12);
		pcp1.setAccNewPatientFlag("Y");
		pcp1.setTierLvl(1);
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("1990-04-26");
		pcp1.setGrpgRltdPadrsTrmntnDt(date);
		pcp1.setRgnlNtwkId("ABC00");
		pcp2.setProvPcpId("AZ4456");
		pcp2.setLatdCordntNbr(21.1234);
		pcp2.setLngtdCordntNbr(32.1234);
		pcpList.add(pcp1);
		pcpList.add(pcp2);
		return pcpList;
	}

	public Member newMember() {

		Member member = new Member();
		Address adrs = new Address();
		List<String> cntrctCode = new ArrayList<>();
		List<String> netwrkId = new ArrayList<>();
		List<String> memberLangCd = new ArrayList<>();

		member.setInvocationSystem("01");
		adrs.setLatitude(11.1234);
		adrs.setLongitude(23.1234);
		member.setAddress(adrs);
		cntrctCode.add("CC86");
		member.setMemberContractCode(cntrctCode);
		member.setMemberDob("1990-04-26");
		member.setMemberGender("M");
		memberLangCd.add("ENG");
		member.setMemberLineOfBusiness("ABD00000");
		netwrkId.add("SSINHM00");
		member.setMemberNetworkId(netwrkId);
		member.setMemberProcessingState("CT");
		member.setMemberType("N");
		member.setSystemType("O");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("Y");
		member.setMemberEffectiveDate("1989-09-12");

		return member;
	}

	public Member newMember2() {

		Member member = new Member();
		List<String> cntrctCode = new ArrayList<>();
		List<String> memberLangCd = new ArrayList<>();

		member.setInvocationSystem("01");
		cntrctCode.add("CC86");
		member.setMemberContractCode(cntrctCode);
		member.setMemberDob("1990-04-26");
		member.setMemberGender("M");
		memberLangCd.add("ENG");
		member.setMemberLineOfBusiness("ABD00000");
		member.setMemberProcessingState("CT");
		member.setMemberType("N");
		member.setSystemType("O");
		member.setRollOverPcpId("1234");
		member.setMemberPregnancyIndicator("Y");
		member.setMemberEffectiveDate("1989-09-12");

		return member;
	}

	private List<PCP> validPCPNegativeList() throws ParseException {

		List<PCP> pcpList = new ArrayList<>();
		List<String> wgsLandCd = new ArrayList<>();
		PCP pcp1 = new PCP();
		PCP pcp2 = new PCP();

		wgsLandCd.add("ENG");
		pcp1.setProvPcpId("AD1234");
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("1990-04-26");
		pcp1.setGrpgRltdPadrsTrmntnDt(date);
		pcp1.setRgnlNtwkId("ABC00");
		pcp2.setProvPcpId("AZ4456");
		pcpList.add(pcp1);
		pcpList.add(pcp2);
		return pcpList;
	}
}
