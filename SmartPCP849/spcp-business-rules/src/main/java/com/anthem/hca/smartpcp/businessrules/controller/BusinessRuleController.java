package com.anthem.hca.smartpcp.businessrules.controller;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anthem.hca.smartpcp.businessrules.util.LoggerUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class BusinessRuleController {

	private static final Logger logger = LoggerFactory.getLogger(BusinessRuleController.class);

	@ApiOperation(value = "Get File as a Resource in the Response Body", response = ResponseEntity.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "SUCCESS"),
    		@ApiResponse(code = 404, message = "RESOURCE_NOT_FOUND")
        }
    )
	@RequestMapping(value = "/file/{fileName:.+}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
		String filePath = "rules/" + fileName;
        Resource resource = new ClassPathResource(filePath);
        
        ResponseEntity<Resource> responseEntity = null;
        try (InputStream in = resource.getInputStream()) {
        	responseEntity =  ResponseEntity.ok()
            		.contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
        	String msg = LoggerUtils.cleanMessage("File: " + fileName + " not found in Business Rules Microservice");
        	logger.error(msg, e);
        	responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    	return responseEntity;
    }

}
