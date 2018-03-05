package com.anthem.emep.dckr.microsvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.emep.dckr.microsvc.service.BingService;

@RestController
public class BingController {
	
	 @Autowired
     private BingService bingService;
	
	 @RequestMapping(value = "bing")
     public Object getDistanceMatrix(@RequestParam("origin") String origin,@RequestParam("destination") String destination) {

		return bingService.getDrivingDistance(origin, destination);
     }

}
