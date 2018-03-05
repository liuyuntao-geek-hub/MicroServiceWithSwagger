package com.hivePoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hivePoc.model.HiveBO;
import com.hivePoc.service.IHiveService;
import com.hivePoc.service.HiveServiceImpl;

@RestController
@RequestMapping(value="/hive")
public class HiveController {

	@Autowired
	private IHiveService hiveservice;
	
	@RequestMapping(value="/data",method=RequestMethod.GET)
	public List<HiveBO> getData(){
		return hiveservice.getData();
	}
}
