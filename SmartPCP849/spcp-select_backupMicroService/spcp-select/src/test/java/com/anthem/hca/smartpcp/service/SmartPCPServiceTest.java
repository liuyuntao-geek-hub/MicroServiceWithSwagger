package com.anthem.hca.smartpcp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.constants.ProductPlanConstants;
import com.anthem.hca.smartpcp.constants.ResponseCodes;
import com.anthem.hca.smartpcp.helper.BingHelper;
import com.anthem.hca.smartpcp.helper.DateUtils;
import com.anthem.hca.smartpcp.helper.PayloadHelper;
import com.anthem.hca.smartpcp.model.Address;
import com.anthem.hca.smartpcp.model.AffinityInputPayload;
import com.anthem.hca.smartpcp.model.AffinityOutPayload;
import com.anthem.hca.smartpcp.model.MDOInputPayload;
import com.anthem.hca.smartpcp.model.MDOOutputPayload;
import com.anthem.hca.smartpcp.model.Member;
import com.anthem.hca.smartpcp.model.OperationsAuditUpdate;
import com.anthem.hca.smartpcp.model.OutputPayload;
import com.anthem.hca.smartpcp.model.PCP;
import com.anthem.hca.smartpcp.model.Reporting;
import com.anthem.hca.smartpcp.model.RulesInputPayload;
import com.anthem.hca.smartpcp.model.ServiceStatus;
import com.anthem.hca.smartpcp.model.SmartPCPRules;
import com.anthem.hca.smartpcp.model.SmartPCPRulesOutputPayload;
import com.anthem.hca.smartpcp.model.Status;
import com.anthem.hca.smartpcp.repository.ProductTypeRepo;

public class SmartPCPServiceTest {

	@InjectMocks
	private SmartPCPService smartPCPService;

	@Mock
	private BingHelper bingHelper;

	@Mock
	private RestClientService restClientService;
	
	@Mock
	private OperationAuditService auditService;
	
	@Mock
	private OutputPayloadService outputPayloadHelper;
	
	@Mock
	private ProductTypeRepo productTypeRepo;
	
	@Mock
	private PayloadHelper payloadHelper;
	
	
	@Mock
	private DateUtils dateUtils;
	
	@Mock
	private AsyncService asyncService;
	


	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPCPAM1() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");



		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");

		MDOOutputPayload mdoPayload = new MDOOutputPayload();

		mdoPayload.setMdoScore(650);
		mdoPayload.setPcpId("ABCD189");
		mdoPayload.setDrivingDistance(2.345);
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");
		mdoPayload.setDummyFlag(false);
		mdoPayload.setPcpNtwrkId("ASD124");
		
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class), any(String.class))).thenReturn(1);
		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_MDO);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_MDO, out.getReporting().getReportingCode());

	}

	@Test
	public void testGetPCPAM2() throws IOException {
		
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");


		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");

		MDOOutputPayload mdoPayload = new MDOOutputPayload();

		mdoPayload.setResponseCode("700");
		mdoPayload.setResponseMessage("Error");
		
		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus= new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class), any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");

		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPAM3() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");
		affinityPayload.setPcpId("ABCD189");

		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class), any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_AFFINITY, out.getReporting().getReportingCode());

	}

	@Test
	public void testGetPCPAM4() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");

		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_MDO);
		output.setReporting(reporting);
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		
		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("700");
		
		MDOOutputPayload mdoPayload = new MDOOutputPayload();

		mdoPayload.setMdoScore(650);
		mdoPayload.setPcpId("ABCD189");
		mdoPayload.setDrivingDistance(2.345);
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");
		mdoPayload.setDummyFlag(false);
		mdoPayload.setPcpNtwrkId("ASD124");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class), any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_MDO, out.getReporting().getReportingCode());

	}
	
	@Test
	public void testGetPCPAM5() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");


		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");
		affinityPayload.setPcpId("ABCD189");
		Map<String,String> attributes = new HashMap<>();
		
		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(productTypeRepo.getProductTypeProd(any(String.class))).thenReturn("");
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_AFFINITY, out.getReporting().getReportingCode());

	}
	
	@Test
	public void testGetPCPAM6() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus= new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		Map<String,String> attributes = new HashMap<>();
		
		
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("700");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(productTypeRepo.getProductTypeProd(any(String.class))).thenReturn("");
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals(ResponseCodes.SERVICE_NOT_AVAILABLE, out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPMA1() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("MA");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");

		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		
		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");
		affinityPayload.setPcpId("ABCD189");

		MDOOutputPayload mdoPayload = new MDOOutputPayload();
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_AFFINITY, out.getReporting().getReportingCode());

	}

	@Test
	public void testGetPCPMA2() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("MA");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		
		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("700");

		MDOOutputPayload mdoPayload = new MDOOutputPayload();
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");

		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPMA3() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("MA");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");


		MDOOutputPayload mdoPayload = new MDOOutputPayload();
		mdoPayload.setMdoScore(10);
		mdoPayload.setDummyFlag(false);
		mdoPayload.setPcpId("ABCD189");
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_MDO);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals(Constants.REPORTING_CODE_MDO, out.getReporting().getReportingCode());
		assertEquals("ABCD189", out.getProvider().getProvPcpId());

	}

	@Test
	public void testGetPCPMA4() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("MA");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");


		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		MDOOutputPayload mdoPayload = new MDOOutputPayload();

		mdoPayload.setResponseCode("700");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPA1() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("A");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");
		affinityPayload.setPcpId("ABCD189");

		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_AFFINITY);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_AFFINITY, out.getReporting().getReportingCode());

	}

	@Test
	public void testGetPCPA2() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("A");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("700");

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPM1() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("M");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");

		MDOOutputPayload mdoPayload = new MDOOutputPayload();
		mdoPayload.setMdoScore(10);
		mdoPayload.setPcpId("ABCD189");
		mdoPayload.setResponseCode("200");
		mdoPayload.setResponseMessage("Success");

		OutputPayload output = new OutputPayload();
		Reporting reporting = new Reporting();
		reporting.setReportingCode(Constants.REPORTING_CODE_MDO);
		output.setReporting(reporting);
		PCP pcp = new PCP();
		pcp.setProvPcpId("ABCD189");
		output.setProvider(pcp);
		doNothing().when(asyncService).asyncMemberUpdate(any(Member.class));
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createSuccessPaylod(any(String.class),any(String.class), any(Member.class), any(String.class),  any(boolean.class),  any(String.class),  any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("ABCD189", out.getProvider().getProvPcpId());
		assertEquals(Constants.REPORTING_CODE_MDO, out.getReporting().getReportingCode());

	}

	@Test
	public void testGetPCPM2() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("M");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		MDOOutputPayload mdoPayload = new MDOOutputPayload();
		mdoPayload.setResponseCode("700");

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPMDO(any(MDOInputPayload.class))).thenReturn(mdoPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPDroolsErrorResponse1() throws IOException {
		Member member = createMember();	
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("M");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(700);
		rulesOutputpayload.setResponseMessage("Success");
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}

	@Test
	public void testGetPCPDroolsErrorResponse2() throws IOException {
		Member member = createMember();	
		
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("M");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(700);
		rulesOutputpayload.setResponseMessage("Success");

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setErrorCode("700");
		status.setService(serviceStatus);
		output.setStatus(status);
		Map<String,String> attributes = new HashMap<>();
		

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(1);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		Mockito.when(productTypeRepo.getProductTypeProd(any(String.class))).thenReturn("HMO");
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("700", out.getStatus().getService().getErrorCode());

	}
	
	@Test
	public void testGetPCPInsertFail() throws IOException {
		
		Member member = createMember();

		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(0);
		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus= new ServiceStatus();
		serviceStatus.setErrorCode("800");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("800", out.getStatus().getService().getErrorCode());

	}
	
	@Test
	public void testGetPCPUpdateFail() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("AM");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");


		AffinityOutPayload affinityPayload = new AffinityOutPayload();
		affinityPayload.setResponseCode("200");
		affinityPayload.setPcpId("ABCD189");

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus= new ServiceStatus();
		serviceStatus.setErrorCode("800");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		Map<String,String> attributes = new HashMap<>();
		attributes.put(ProductPlanConstants.PLAN_ST_CD_LABEL, "CA");
		attributes.put(ProductPlanConstants.PROD_FMLY_TYPE_CD_LABEL, "HMO");
		
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class),any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(0);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(restClientService.getPCPAffinity(any(AffinityInputPayload.class))).thenReturn(affinityPayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("800", out.getStatus().getService().getErrorCode());

	}
	
	@Test
	public void testGetPCPInvocationOrderFail() throws IOException {
		Member member = createMember();
		SmartPCPRulesOutputPayload rulesOutputpayload = new SmartPCPRulesOutputPayload();
		SmartPCPRules rules = new SmartPCPRules();
		rules.setInvocationOrder("");
		rulesOutputpayload.setRules(rules);
		rulesOutputpayload.setResponseCode(200);
		rulesOutputpayload.setResponseMessage("Success");
		
		Map<String,String> attributes = new HashMap<>();
		

		OutputPayload output = new OutputPayload();
		Status status = new Status();
		ServiceStatus serviceStatus= new ServiceStatus();
		serviceStatus.setErrorCode("800");
		status.setService(serviceStatus);
		output.setStatus(status);
		
		Mockito.when(auditService.insertOperationFlow(any(Member.class))).thenReturn(1);
		Mockito.when(auditService.updateOperationFlow(any(OutputPayload.class), any(OperationsAuditUpdate.class), any(String.class),any(String.class))).thenReturn(0);
		Mockito.when(bingHelper.prepareDynamicURL(any(Address.class), any(String.class), any(String.class))).thenReturn("ASD");
		Mockito.when(bingHelper.retrieveGeocode( any(String.class),any(Member.class))).thenReturn(member);
		Mockito.when(restClientService.getGeocodes(any(String.class))).thenReturn("ASDF");
		Mockito.when(restClientService.getInvocationOrder(any(RulesInputPayload.class))).thenReturn(rulesOutputpayload);
		Mockito.when(outputPayloadHelper.createErrorPayload(any(String.class), any(String.class), any(Member.class), any(Integer.class))).thenReturn(output);
		Mockito.when(productTypeRepo.getProductTypePlan(any(String.class), any(String.class))).thenReturn(attributes);
		OutputPayload out = smartPCPService.getPCP(member);
		assertEquals("800", out.getStatus().getService().getErrorCode());

	}
	
	
	
	private Member createMember(){
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
		address.setZipCode("1234");
		address.setZipFour("1234");
		address.setLatitude(23.45);
		address.setLongitude(-164.56);
		member.setAddress(address);
		member.setMemberProduct("14EM");
		member.setMemberDob("1990-07-23");
		return member;
	}

}
