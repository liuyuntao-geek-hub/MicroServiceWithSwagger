package com.anthem.hca.smartpcp.mdoscoring.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class TieBreakerUtil {

	private static final Logger logger = LoggerFactory.getLogger(TieBreakerUtil.class);

	public List<PCP> getNearestPCP(List<PCP> pcpInfo) {
		Collections.sort(pcpInfo, PCP.drivingDistComprtr);

		double shortestDist = pcpInfo.get(0).getDrivingDistance();
		List<PCP> shortestDistPCP = new ArrayList<>();

		for (PCP pcp : pcpInfo) {
			if (pcp.getDrivingDistance() == shortestDist) {
				shortestDistPCP.add(pcp);
			}
		}

		logger.info("Number of PCPs returned {}", shortestDistPCP.size());
		return shortestDistPCP;
	}

	public List<PCP> getVbpPcp(List<PCP> nearestPCP) {

		List<PCP> vbpAgreedPCP = new ArrayList<>();
		for (PCP pcp : nearestPCP) {
			if ((Constant.VBP_FLAG).equalsIgnoreCase(pcp.getVbpFlag())) {
				vbpAgreedPCP.add(pcp);
			}
		}

		logger.info("Number of PCPs returned: {} " , vbpAgreedPCP.size());
		return vbpAgreedPCP;
	}

	public List<PCP> getleastAssignedPCP(List<PCP> vbpPCPList) {
		Collections.sort(vbpPCPList, PCP.panelCapacityComprtr);

		float leastPanelCapacity = vbpPCPList.get(0).getPanelCapacity();
		List<PCP> leastAssignedPCP = new ArrayList<>();

		for (PCP pcp : vbpPCPList) {
			if (leastPanelCapacity ==pcp.getPanelCapacity() ) {
				leastAssignedPCP.add(pcp);
			}
		}
		logger.info("Number of PCPs returned: {}" , leastAssignedPCP.size());
		return leastAssignedPCP;
	}

	public List<PCP> getAlphabeticallySortedPCPList(List<PCP> leastAssignedPCPList) {
		List<PCP> validatedProviderList = leastAssignedPCPList.stream().filter(pcp -> {
			boolean flag = false;
			if (!StringUtils.isBlank(pcp.getPcpLastNm())) {
				flag = true;
			}
			return flag;
		}).collect(Collectors.toList());
		logger.info("In Alphabetical Sorting Tie Breaker logic.{}"," ");
		Collections.sort(validatedProviderList, PCP.lastNameComparator);
		return validatedProviderList;
	}

}
