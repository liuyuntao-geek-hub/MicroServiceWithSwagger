package com.anthem.emep.dckr.microsvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.emep.dckr.microsvc.model.Discount;
import com.anthem.emep.dckr.microsvc.service.DiscountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class DiscountController {
	
	@Autowired
	public DiscountService service;
	
	@Autowired
	public ObjectMapper mapper;
	
	@RequestMapping("/discount/{key}")
	public String getDiscountDetails(@PathVariable Integer key) throws JsonProcessingException{
		
		Discount d = service.getDiscount(key);
		if(null != d){
			return mapper.writeValueAsString(d);
		}
		
		return "";
		
	}
	
	
	@RequestMapping(value = "/discount/single",method=RequestMethod.POST)
	public String getDiscountDetailSingleObject(@RequestBody Discount data) throws Exception{
			
		Discount d = service.getDiscountValue(data);
		if(null != d){
				return mapper.writeValueAsString(d);
			}
		
		return "";
		
	}
	
	@RequestMapping("/discount")
	public void insertDetails(){
		service.insertValues();
	}
}
