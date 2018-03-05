package com.anthem.emep.dckr.microsvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.emep.dckr.microsvc.service.AerialDistanceService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class AerialDistanceController {

	  @Autowired
      public AerialDistanceService service;

      @RequestMapping(value="getdistance")
      public Object getDistances(@RequestParam("lat") String lat,@RequestParam("lon") String lon) throws JsonProcessingException{
      
		//String result = service.distance(Double.parseDouble(lat), Double.parseDouble(lon));

		 String result = service.distanceJava(Double.parseDouble(lat),Double.parseDouble(lon));

		return result;
      }
}
