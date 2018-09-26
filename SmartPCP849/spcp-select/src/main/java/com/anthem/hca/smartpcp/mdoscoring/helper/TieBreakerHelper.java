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
package com.anthem.hca.smartpcp.mdoscoring.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import com.anthem.hca.smartpcp.constants.Constants;
import com.anthem.hca.smartpcp.model.PCPAssignmentFlow;
import com.anthem.hca.smartpcp.model.ScoringProvider;


@Configuration
public class TieBreakerHelper {
	
	public List<ScoringProvider>  getNearestPCP(List<ScoringProvider> pcpInfo, PCPAssignmentFlow pcpAssignmentFlow) {
		Collections.sort(pcpInfo, ScoringProvider.distanceComprtr);
		double shortestDist = pcpInfo.get(0).getDistance();
		List<ScoringProvider> shortestDistPCP = new ArrayList<>();
		
		for (ScoringProvider pcp : pcpInfo) {
			if (shortestDist==pcp.getDistance()) {
				shortestDistPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnDrivingPcps(shortestDistPCP);
		return shortestDistPCP;
	}

	public List<ScoringProvider> getVbpPcp(List<ScoringProvider> nearestPCP, PCPAssignmentFlow pcpAssignmentFlow) {
		List<ScoringProvider> vbpAgreedPCP = new ArrayList<>();
		for (ScoringProvider pcp : nearestPCP) {
			if ((Constants.VBP_FLAG).equalsIgnoreCase(pcp.getVbpFlag())) {
				vbpAgreedPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnVBPPcps(vbpAgreedPCP);
		return vbpAgreedPCP;
	}

	public List<ScoringProvider> getleastAssignedPCP(List<ScoringProvider> vbpPCPList, PCPAssignmentFlow pcpAssignmentFlow) {
		Collections.sort(vbpPCPList, ScoringProvider.panelCapacityComprtr);
		double leastPanelCapacity = vbpPCPList.get(0).getPanelCapacity();
		List<ScoringProvider> leastAssignedPCP = new ArrayList<>();
		for (ScoringProvider pcp : vbpPCPList) {
			if (leastPanelCapacity == pcp.getPanelCapacity()) {
				leastAssignedPCP.add(pcp);
			}
		}
		pcpAssignmentFlow.setTieOnPanelCapacityPcps(leastAssignedPCP);
		return leastAssignedPCP;
	}

	public List<ScoringProvider> getAlphabeticallySortedPCPList(List<ScoringProvider> leastAssignedPCPList, PCPAssignmentFlow pcpAssignmentFlow) {
		List<ScoringProvider> pcpDtls;
		List<ScoringProvider> validatedProviderList = leastAssignedPCPList.stream().filter(pcp -> {
			boolean flag = false;
			if (!StringUtils.isBlank(pcp.getLastName())) {
				flag = true;
			}
			return flag;
		}).collect(Collectors.toList());
		if(!validatedProviderList.isEmpty()){
		Collections.sort(validatedProviderList, ScoringProvider.lastNameComparator);
		pcpDtls=  validatedProviderList;
		}
		else{
			pcpDtls= leastAssignedPCPList;
		}
		pcpAssignmentFlow.setTieOnLastNamePcps(pcpDtls);
		return pcpDtls;
	}

}
