package com.deloitte.demo.controllerAPI;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import com.deloitte.demo.execution.service.PCPService;
import com.deloitte.demo.model.entity.PCP;

@Controller
public class PCPController {

    @Autowired
    private PCPService service;

    @ApiOperation(value = "Get all PCPS from Splice Machine", response = Iterable.class)
    @ApiResponses(
    	value = { 
    		@ApiResponse(code = 200, message = "OK", response = Iterable.class),
    		@ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
    		@ApiResponse(code = 403, message = "Forbidden", response = Void.class),
    		@ApiResponse(code = 404, message = "Not Found", response = Void.class) 
        }
    )
    @RequestMapping(value = "/PCP/getAllPCPsFromSplice/{latitude}/{longitude}", method = RequestMethod.GET, produces = { "application/json" })
    public ResponseEntity<Iterable<PCP>> fetchPCPSUsingGET(
    		@ApiParam(value = "memberLatitude", required = true ) @PathVariable("latitude") Double latitude,
    		@ApiParam(value = "memberLongitude", required = true ) @PathVariable("longitude") Double longitude
    ) {
        Iterable<PCP> pcps = service.listAllPCPS(latitude, longitude);
        return new ResponseEntity<Iterable<PCP>>(pcps, HttpStatus.OK);
    }

}
