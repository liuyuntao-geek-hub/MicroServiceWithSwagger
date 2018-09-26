/**
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information  
 * 
 * Description - Functions for tie breaker logic
 * 
 * @author AF70896
 * 
 */
package com.anthem.hca.smartpcp.mdoscoring.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.anthem.hca.smartpcp.mdoscoring.vo.PCP;
import com.anthem.hca.smartpcp.mdoscoring.vo.PCPAssignmentFlow;

public class TieBreakerUtil {
	
	public List<PCP> getNearestPCP(List<PCP> pcpInfo, PCPAssignmentFlow pcpAssignmentFlow) {
		Collections.sort(pcpInfo, PCP.drivingDistComprtr);
		double shortestDist = pcpInfo.get(0).getDrivingDistance();
		List<PCP> shortestDistPCP = new ArrayList<>();
		for (PCP pcp : pcpInfo) {
			if (shortestDist==pcp.getDrivingDistance()) {
				shortestDistPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnDrivingPcps(shortestDistPCP);
		return shortestDistPCP;
	}

	public List<PCP> getVbpPcp(List<PCP> nearestPCP, PCPAssignmentFlow pcpAssignmentFlow) {
		List<PCP> vbpAgreedPCP = new ArrayList<>();
		for (PCP pcp : nearestPCP) {
			if ((Constant.VBP_FLAG).equalsIgnoreCase(pcp.getVbpFlag())) {
				vbpAgreedPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnVBPPcps(vbpAgreedPCP);
		return vbpAgreedPCP;
	}

	public List<PCP> getleastAssignedPCP(List<PCP> vbpPCPList, PCPAssignmentFlow pcpAssignmentFlow) {
		Collections.sort(vbpPCPList, PCP.panelCapacityComprtr);
		double leastPanelCapacity = vbpPCPList.get(0).getPanelCapacity();
		List<PCP> leastAssignedPCP = new ArrayList<>();
		for (PCP pcp : vbpPCPList) {
			if (leastPanelCapacity == pcp.getPanelCapacity()) {
				leastAssignedPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnPanelCapacityPcps(leastAssignedPCP);
		return leastAssignedPCP;
	}

	public List<PCP> getAlphabeticallySortedPCPList(List<PCP> leastAssignedPCPList, PCPAssignmentFlow pcpAssignmentFlow) {
		List<PCP> pcpDtls;
		List<PCP> validatedProviderList = leastAssignedPCPList.stream().filter(pcp -> {
			boolean flag = false;
			if (!StringUtils.isBlank(pcp.getPcpLastNm())) {
				flag = true;
			}
			return flag;
		}).collect(Collectors.toList());
		if(!validatedProviderList.isEmpty()){
		Collections.sort(validatedProviderList, PCP.lastNameComparator);
		pcpDtls=  validatedProviderList;
		}
		else{
			pcpDtls= leastAssignedPCPList;
		}
		pcpAssignmentFlow.setTieOnLastNamePcps(pcpDtls);
		return pcpDtls;
	}

}
