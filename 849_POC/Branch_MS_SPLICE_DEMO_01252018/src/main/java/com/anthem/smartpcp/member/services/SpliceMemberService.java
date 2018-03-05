package com.anthem.smartpcp.member.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.smartpcp.member.model.MBR;
import com.anthem.smartpcp.member.repository.SpliceMemberRepository;

@Service
public class SpliceMemberService {

	@Autowired
	private SpliceMemberRepository repo;

    public MBR getMBR(long key) {
		return repo.findByKey(key);
	}

    public void persistMBR(MBR mbr) {
    	repo.save(mbr); // persist data
    }
}
