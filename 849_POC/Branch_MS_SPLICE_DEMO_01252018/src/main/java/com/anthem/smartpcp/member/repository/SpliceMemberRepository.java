package com.anthem.smartpcp.member.repository;

import org.springframework.data.repository.CrudRepository;

import com.anthem.smartpcp.member.model.MBR;

public interface SpliceMemberRepository extends CrudRepository<MBR, Long> {

	public MBR findByKey(long key);  // override

}
