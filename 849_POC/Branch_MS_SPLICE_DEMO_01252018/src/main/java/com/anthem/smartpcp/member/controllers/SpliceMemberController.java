package com.anthem.smartpcp.member.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.anthem.smartpcp.member.model.MBR;
import com.anthem.smartpcp.member.services.SpliceMemberService;

@RestController
@Api(value = "SpliceMachine Member Service APIs")
public class SpliceMemberController {

    @Autowired
    private SpliceMemberService service;

    /*@ApiOperation(value = "Get Member details", response = MBR.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "OK", response = MBR.class),
    		@ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
    		@ApiResponse(code = 403, message = "Forbidden", response = Void.class),
    		@ApiResponse(code = 404, message = "Not Found", response = Void.class) 
        }
    )*/
    @RequestMapping(value = "/{key}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MBR> getMember(
    		/*@ApiParam(value = "Member Key", required = true )*/ @PathVariable("key") long key
    ) {
    	MBR mbr = service.getMBR(key);
        return new ResponseEntity<MBR>(mbr, HttpStatus.OK);
    }

    /*
     * @ApiOperation(value = "Insert Member details", response = Iterable.class)
    @ApiResponses(
    	value = {
    		@ApiResponse(code = 200, message = "OK", response = Iterable.class),
    	    @ApiResponse(code = 201, message = "Created", response = Iterable.class),
    	    @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
    	    @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
    	    @ApiResponse(code = 404, message = "Not Found", response = Void.class)
        }
    )*/
    @RequestMapping(value = "/persistMBR", method = RequestMethod.POST)
    public ResponseEntity<String> persistMember(@RequestBody MBR mbr) {
        service.persistMBR(mbr);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
