/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.util;

import java.util.Comparator;
import com.anthem.hca.smartpcp.common.am.vo.PCP;

public class PCPComparatorSort implements Comparator<PCP> {

	public int compare(PCP provider1, PCP provider2) {
		if (provider1.getAerialDistance() < provider2.getAerialDistance()) {
			return -1;
		} else if (provider1.getAerialDistance() > provider2.getAerialDistance()) {
			return 1;
		} else
			return 0;
	}
}
