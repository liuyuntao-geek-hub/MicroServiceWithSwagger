package com.anthem.emep.dckr.microsvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.emep.dckr.microsvc.service.DiscountService;

@RestController
public class Controller {

	@Autowired
	public DiscountService service;
	
	@RequestMapping("/details/{key}")
	public String getDetails(@PathVariable Integer key){
		
		return service.getDetails(key);
		
	}
	
}
