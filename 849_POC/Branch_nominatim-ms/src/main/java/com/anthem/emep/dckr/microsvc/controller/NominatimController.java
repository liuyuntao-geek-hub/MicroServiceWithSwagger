package com.anthem.emep.dckr.microsvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.emep.dckr.microsvc.service.NominatimService;

@RestController
public class NominatimController {
	
	@Autowired
    private NominatimService nominatimService;

	@RequestMapping(value = "geocodes", method = RequestMethod.POST)
    public Object getDistance(@RequestParam("houseNo") String houseNo, @RequestParam("street") String street,
                                 @RequestParam("city") String city, @RequestParam("state") String state,
                                 @RequestParam("pincode") String pincode) throws Exception{
		
		return nominatimService.getGeocodes(houseNo, street, city, state, pincode);
		
	}
}
