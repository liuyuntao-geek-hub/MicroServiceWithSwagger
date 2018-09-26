/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.model;

import com.anthem.hca.smartpcp.common.am.vo.Member;

public class RulesInputPayload {

	private Member member;

	public Member getMember() {
		return member;
	}

	public void setMember(Member that) {
		this.member = that;
	}

}
