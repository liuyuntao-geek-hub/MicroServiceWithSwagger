package com.deloitte.demo.execution.repository;

import org.springframework.data.repository.CrudRepository;
import com.deloitte.demo.model.entity.PCP;

public interface PCPRepository extends CrudRepository<PCP, Long> {
}
