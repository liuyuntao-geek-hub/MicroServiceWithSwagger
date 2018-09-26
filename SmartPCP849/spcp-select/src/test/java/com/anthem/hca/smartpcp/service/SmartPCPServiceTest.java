package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.affinity.service.AffinityService;
import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.SmartPCPRules;
import com.anthem.hca.smartpcp.drools.service.SmartPCPRulesService;
import com.anthem.hca.smartpcp.drools.util.DroolsParseException;
import com.anthem.hca.smartpcp.helper.BingHelper;
import com.anthem.hca.smartpcp.mdo.pool.service.MDOPoolService;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.model.Provider;
import com.anthem.hca.smartpcp.model.Reporting;
import com.anthem.hca.smartpcp.model.ScoringProvider;
import com.anthem.hca.smartpcp.model.ServiceStatus;
import com.anthem.hca.smartpcp.model.Status;
import com.anthem.hca.smartpcp.model.TransactionStatus;
import com.anthem.hca.smartpcp.repository.GeocodeRepo;
import com.anthem.hca.smartpcp.repository.ProductTypeRepo;
import com.anthem.hca.smartpcp.repository.ProviderInfoRepo;
import com.anthem.hca.smartpcp.util.DateUtils;

public class SmartPCPServiceTest {

	@InjectMocks
	private SmartPCPService smartPCPService;

	@Mock
	private BingHelper bingHelper;

	@Mock
	private OperationAuditService auditService;

	@Mock
	private OutputPayloadService outputPayloadHelper;

	@Mock
	private ProductTypeRepo productTypeRepo;

	@Mock
	private GeocodeRepo geocodeRepo;

	@Mock
	private ProviderInfoRepo providerInfoRepo;

	@Mock
	private AffinityService affinityService;

	@Mock
	private SmartPCPRulesService smartPCPRulesService;

	@Mock
	private MDOPoolService mdoService;

	@Mock
	private DateUtils dateUtils;

	@Mock
	private AsyncService asyncService;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPCPAMForAffinity() throws IOException, DroolsParseException {
		Member member = createMember();

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(rulesPayload());
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(providerPayload());
		Mockito.when(outputPayloadHelper.createSuccessPaylodAffinity(any(Member.class), any(Provider.class)))
				.thenReturn(createoutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("AFNTY123", out.getProvider().getProvPcpId());

	}

	//@Test
	public void testGetPCPAMForMDO() throws Exception {
		Member member = createMember();
		ScoringProvider pcp = scoringProviderPCP();
		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(rulesPayload());
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(mdoService.getValidPCP(any(Member.class))).thenReturn(pcp);
		Mockito.when(outputPayloadHelper.createSuccessPaylodMDO(any(Member.class), any(Boolean.class),
				any(ScoringProvider.class))).thenReturn(createMDOOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("MDO123", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetNullPCPAM() throws IOException, DroolsParseException {
		Member member = createMember();

		Map<String, String> attributes = new HashMap<>();

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(rulesPayload());
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(null);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetPCPA() throws IOException, DroolsParseException {
		Member member = createMember();

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("A");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(pcpRules);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(providerPayload());
		Mockito.when(outputPayloadHelper.createSuccessPaylodAffinity(any(Member.class), any(Provider.class)))
				.thenReturn(createoutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("AFNTY123", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetNullPCPA() throws IOException, DroolsParseException {
		Member member = createMember();

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("A");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(pcpRules);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(null);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetErrorPCP() throws IOException, DroolsParseException {
		Member member = createMember();

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("MA");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(pcpRules);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(providerPayload());
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetNullRulesPCPA() throws IOException, DroolsParseException {
		Member member = createMember();

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(null);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(null);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetAuditError() throws IOException, DroolsParseException {
		Member member = createMember();

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("MA");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(-1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(pcpRules);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(providerPayload());
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetProductType() throws IOException, DroolsParseException {

		Member member = new Member();
		member.setMemberGroupId(123);
		member.setMemberSubGroupId(123);
		member.setMemberEid("ASD");
		member.setMemberLineOfBusiness("AWE1234");
		member.setMemberSequenceNumber("ASD123");
		member.setMemberProcessingState("CA");
		member.setMemberProductType("HES");
		Address address = new Address();
		address.setAddressLine1("STREET");
		address.setCity("Sacramento");
		address.setState("CA");
		address.setZipCode("23005");
		address.setZipFour("1234");
		address.setLatitude(23.45);
		address.setLongitude(-164.56);
		member.setAddress(address);
		member.setMemberProduct("14EM");
		member.setMemberDob("1990-07-23");

		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("A");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");

		Map<String, String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class),
				any(Member.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class)))
				.thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode(any(String.class), any(Member.class))).thenReturn(member);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class), any(Timestamp.class));
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(smartPCPRulesService.getRules(any(Member.class))).thenReturn(null);
		Mockito.when(geocodeRepo.getGeocodeMap()).thenReturn(geoCodes());
		Mockito.when(affinityService.getAffinityOutPayload(any(Member.class))).thenReturn(providerPayload());
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class),
				any(int.class))).thenReturn(createErrorOutputPayload());
		OutputPayload out = smartPCPService.getPCP(member,new Timestamp(new java.util.Date().getTime()));
		assertEquals("ERROR1234", out.getProvider().getProvPcpId());

	}

	public SmartPCPRules rulesPayload() throws DroolsParseException {
		SmartPCPRules pcpRules = new SmartPCPRules();
		pcpRules.setAgendaGroup(AgendaGroup.SMARTPCP);
		pcpRules.setAssignmentMethod("ALL");
		pcpRules.setAssignmentType("ALL");
		pcpRules.setInvocationOrder("AM");
		pcpRules.setLob("Commercial");
		pcpRules.setMarket("ALL");
		pcpRules.setProduct("ALL");
		return pcpRules;

	}

	public Provider providerPayload() {

		Provider provider = new Provider();

		provider.setFirstName("Donald");
		provider.setLastName("Duck");
		provider.setMiddleName("T");
		provider.setPcpPmgIpa("I");
		provider.setProvPcpId("ABCD189");
		provider.setRank(1);
		provider.setPhoneNumber("1234567");
		provider.setRgnlNtwkId("RGN001");
		provider.setTaxId("TAX001");

		return provider;
	}

	public OutputPayload createoutputPayload() {

		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("AM");
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setStatus("S");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		status.setService(serviceStatus);
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_SUCCESS);
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		payload.setReporting(reporting);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		PCP pcp = new PCP();
		pcp.setProvPcpId("AFNTY123");
		pcp.setPcpScore(678);
		pcp.setPcpRank(11);
		payload.setProvider(pcp);

		return payload;
	}

	public OutputPayload createMDOOutputPayload() {

		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("AM");
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setStatus("S");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		status.setService(serviceStatus);
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_SUCCESS);
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_MDO);
		payload.setReporting(reporting);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		PCP pcp = new PCP();
		pcp.setProvPcpId("MDO123");
		pcp.setPcpScore(678);
		pcp.setPcpRank(11);
		payload.setProvider(pcp);

		return payload;
	}

	public OutputPayload createAfoutputPayload() {

		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("A");
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setStatus("S");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		status.setService(serviceStatus);
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.OUTPUT_SUCCESS);
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		payload.setReporting(reporting);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		PCP pcp = new PCP();
		pcp.setProvPcpId("AFNTY123");
		pcp.setPcpScore(678);
		pcp.setPcpRank(11);
		payload.setProvider(pcp);

		return payload;
	}

	public OutputPayload createErrorOutputPayload() {

		OutputPayload payload = new OutputPayload();
		OperationsAuditUpdate operationsAudit = new OperationsAuditUpdate();
		operationsAudit.setInvocationOrder("MA");
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setStatus("U");
		operationsAudit.setDrivingDistance(2.09);
		Status status = new Status();
		status.setService(serviceStatus);
		TransactionStatus tranStatus = new TransactionStatus();
		tranStatus.setStatus(Constants.FAILURE);
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		payload.setReporting(reporting);
		status.setTransaction(tranStatus);
		payload.setStatus(status);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ERROR1234");
		pcp.setPcpScore(678);
		pcp.setPcpRank(11);
		payload.setProvider(pcp);

		return payload;
	}

	private Member createMember() {
		Member member = new Member();
		member.setMemberGroupId(123);
		member.setMemberSubGroupId(123);
		member.setMemberEid("ASD");
		member.setMemberLineOfBusiness("AWE1234");
		member.setMemberSequenceNumber("ASD123");
		Address address = new Address();
		address.setAddressLine1("STREET");
		address.setCity("Sacramento");
		address.setState("CA");
		address.setZipCode("23005");
		address.setZipFour("1234");
		address.setLatitude(23.45);
		address.setLongitude(-164.56);
		member.setAddress(address);
		member.setMemberProduct("14EM");
		member.setMemberDob("1990-07-23");
		return member;
	}

	public ScoringProvider scoringProviderPCP() {

		ScoringProvider pcp = new ScoringProvider();
		pcp.setProvPcpId("MDO123");
		pcp.setRgnlNtwkId("HARSH01");
		pcp.setLastName("MDO_LAST");
		pcp.setDistance(7.0680473834351885);
		pcp.setDummyFlag(false);
		pcp.setRank(3);
		pcp.setVbpFlag("Y");
		pcp.setPcpScore(115);
		pcp.setVbpScore(20);
		pcp.setDistanceScore(40);
		pcp.setLimitedTimeBonusScore(15);
		pcp.setLanguageScore(10);
		pcp.setRankScore(30);
		List<String> langList = new ArrayList<>();
		langList.add("SPA");
		List<String> spcltyList = new ArrayList<>();
		spcltyList.add("Internal Medicine");
		pcp.setPcpLang(langList);
		pcp.setSpeciality(spcltyList);
		return pcp;
	}

	public Map<String, Address> geoCodes() {

		Map<String, Address> geocodes = new HashMap<>();
		Address address = new Address();
		address.setZipCode("23005");
		address.setLatitude(23.234);
		address.setLongitude(-164.0921);
		geocodes.put("23005", address);

		return geocodes;
	}
}