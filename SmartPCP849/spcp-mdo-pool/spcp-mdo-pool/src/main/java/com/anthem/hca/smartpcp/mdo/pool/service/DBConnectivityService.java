/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.anthem.hca.smartpcp.common.am.vo.Member;
import com.anthem.hca.smartpcp.common.am.vo.PCP;
import com.anthem.hca.smartpcp.mdo.pool.repository.ProviderInfoRepo;

/**
 * @author AF71111
 *
 *         Intermediate class that maintains the splice connectivity.
 *
 */

@Service
public class DBConnectivityService {

	@Autowired
	private ProviderInfoRepo providerInfoRepo;

	/**
	 * @param memberNtwrkIds
	 * @return
	 */
	public List<PCP> getPCPDetails(List<String> memberNtwrkIds, Member member) {
		return providerInfoRepo.getPCPDtlsAsList(memberNtwrkIds,member);
	}

	@Bean
	public ProviderInfoRepo getProviderInfoRepo() {
		return new ProviderInfoRepo();
	}
}
