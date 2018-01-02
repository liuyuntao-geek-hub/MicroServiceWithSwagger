/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.deloitte.demo.controllerAPI;

//import com.deloitte.demo.modelEntity.Iterable;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-21T05:48:01.403Z")
//@javax.annotation.Generated(value = "com.deloitte.demo.codegen.languages.SpringCodegen", date = "2017-12-21T05:48:01.403Z")

@Api(value = "PCPAssignment", description = "the PCPAssignment API")
public interface PCPAssignmentApi {

    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 204, message = "No Content", response = Void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })

    @RequestMapping(value = "/PCPAssignment/",
            produces = { "*/*" },
          //  consumes = { "application/json" },
            method = RequestMethod.OPTIONS)
    ResponseEntity<String> rootUsingOPTIONS1();


    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class) })

    @RequestMapping(value = "/PCPAssignment/",
            produces = { "*/*" },
           // consumes = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity<String> rootUsingGET1();


    @ApiOperation(value = "listAllPatient", notes = "", response = Iterable.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Iterable.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/listAllPatient",
        produces = { "application/json" }, 
     //   consumes = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<Iterable> listAllPatientUsingGET();


    @ApiOperation(value = "Update Patient with PCP Assignment", notes = "", response = Iterable.class, tags={ "pcp-assignment", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Iterable.class),
            @ApiResponse(code = 201, message = "Created", response = Void.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
            @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
            @ApiResponse(code = 404, message = "Not Found", response = Void.class) })

    @RequestMapping(value = "/PCPAssignment/update/{id}",
            produces = { "application/json" },
            consumes = { "text/plain" },
            method = RequestMethod.PUT)
    ResponseEntity<Iterable> updateProductUsingPUT(@ApiParam(value = "id",required=true ) @PathVariable("id") Integer id,@ApiParam(value = "PCPName" ,required=true )  @Valid @RequestBody String pcPName);

/*
    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.DELETE)
    ResponseEntity<String> rootUsingDELETE1();
*/




/*    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.HEAD)
    ResponseEntity<String> rootUsingHEAD1();*/



/*

    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.PATCH)
    ResponseEntity<String> rootUsingPATCH1();
*/

/*

    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 201, message = "Created", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<String> rootUsingPOST1();
*/

/*

    @ApiOperation(value = "root", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 201, message = "Created", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<String> rootUsingPUT1();
*/

/*

    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.DELETE)
    ResponseEntity<String> sayHelloUsingDELETE1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.GET)
    ResponseEntity<String> sayHelloUsingGET1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.HEAD)
    ResponseEntity<String> sayHelloUsingHEAD1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.OPTIONS)
    ResponseEntity<String> sayHelloUsingOPTIONS1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 204, message = "No Content", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.PATCH)
    ResponseEntity<String> sayHelloUsingPATCH1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 201, message = "Created", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<String> sayHelloUsingPOST1();


    @ApiOperation(value = "sayHello", notes = "", response = String.class, tags={ "pcp-assignment", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = String.class),
        @ApiResponse(code = 201, message = "Created", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "Not Found", response = Void.class) })
    
    @RequestMapping(value = "/PCPAssignment/sayHello",
        produces = { "**" },
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<String> sayHelloUsingPUT1();
*/


}
