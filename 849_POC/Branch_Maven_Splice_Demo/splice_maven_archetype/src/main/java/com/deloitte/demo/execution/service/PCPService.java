package com.deloitte.demo.execution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deloitte.demo.execution.repository.PCPRepository;
import com.deloitte.demo.model.entity.PCP;

@Service
public class PCPService {

	@Autowired
	private PCPRepository repo;

    public Iterable<PCP> listAllPCPS(double latitude, double longitude) {
		return repo.findAll();
	}

}
