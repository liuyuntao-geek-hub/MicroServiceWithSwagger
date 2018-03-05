package com.anthem.pcp.provider.controllers;


import com.anthem.pcp.provider.model.Provider;
import com.anthem.pcp.provider.services.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "v1/providers")
public class ProviderServiceController {

    @Autowired
    private ProviderService providerService;

    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getProvider() {
        logger.debug(String.format("Looking up data for org "));
        return "Service is up";
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Provider getProvider(@PathVariable("memberId") String memberId) {
        logger.debug(String.format("Looking up data for org {}", memberId));
        Provider provider = providerService.getProvider();
        return provider;
    }

}
