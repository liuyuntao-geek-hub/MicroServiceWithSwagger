package com.hivePoc.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hivePoc.model.HiveBO;

@Component("HiveService")
public interface IHiveService {
	public List<HiveBO> getData();
}
