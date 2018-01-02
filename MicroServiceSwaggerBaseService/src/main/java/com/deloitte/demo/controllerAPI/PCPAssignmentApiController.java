package com.deloitte.demo.controllerAPI;

import com.deloitte.demo.execution.service.PCPAssignment.PatientService;
//import com.deloitte.demo.modelEntity.Iterable;

import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-21T05:48:01.403Z")
//@javax.annotation.Generated(value = "com.deloitte.demo.codegen.languages.SpringCodegen", date = "2017-12-21T05:48:01.403Z")

@Controller
public class PCPAssignmentApiController implements PCPAssignmentApi {


    private PatientService patientService;

    @Autowired
    public void setPatientService(PatientService patientService)
    {
        patientService.initPatient();
        this.patientService = patientService;
    }



    public ResponseEntity<String> rootUsingGET1() {
        // do some magic!
        String result = "../PCPAssignment/ GET = ResponseEntity<String>rootUsingGET1\n" +
                "../PCPAssignment/listAllPatient GET = Function to Update the whole object resource <listAllPatientUsingGET> \n" +
                "../PCPAssignment/updateProduct PUT = Function to Update the whole object resource <updateProductUsingPUT> \n" +

                "../PCPAssignment/* PATCH = Function to update portion of the object resource (Not Exist) \n" +
                "../PCPAssignment/* DELETE = Function to Delete the object resource (Not Exist) \n" +
                "../PCPAssignment/* HEAD = Function to return the Header Info (Not Exist) \n" +
                "../PCPAssignment/* POST = Function to create object resource (Not Exist)\n" ;

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingOPTIONS1() {
        // do some magic!
        String result = null;

        result = "../PCPAssignment/ GET = ResponseEntity<String>rootUsingGET1\n" +
                "../PCPAssignment/listAllPatient GET = Function to Update the whole object resource <listAllPatientUsingGET> \n" +
                "../PCPAssignment/updateProduct PUT = Function to Update the whole object resource <updateProductUsingPUT> \n" +

                "../PCPAssignment/* PATCH = Function to update portion of the object resource (Not Exist) \n" +
                "../PCPAssignment/* DELETE = Function to Delete the object resource (Not Exist) \n" +
                "../PCPAssignment/* HEAD = Function to return the Header Info (Not Exist) \n" +
                "../PCPAssignment/* POST = Function to create object resource (Not Exist)\n" ;

        MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();

        header.set("Allow","GET,PUT,OPTIONS");
        header.set("Content-Type", "application/json");


        return new ResponseEntity<String>(result, header, HttpStatus.OK);
    }


    public ResponseEntity<Iterable> listAllPatientUsingGET() {
        // do some magic!
        Iterable patents = (Iterable) patientService.listAllPatient();
        return new ResponseEntity<Iterable>(patents, HttpStatus.OK);
    }

    public ResponseEntity<Iterable> updateProductUsingPUT(@ApiParam(value = "id",required=true ) @PathVariable("id") Integer id,
                                                          @ApiParam(value = "PCPName" ,required=true )  @Valid @RequestBody String pcPName) {
        // do some magic!
        patientService.updatePCP(id,pcPName);
        Iterable patents = patientService.listAllPatient();
        return new ResponseEntity<Iterable>(patents,HttpStatus.OK);
    }

    /*
    public ResponseEntity<String> rootUsingDELETE1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }


    public ResponseEntity<String> rootUsingHEAD1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }


    public ResponseEntity<String> rootUsingPATCH1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingPOST1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingPUT1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingDELETE1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingGET1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingHEAD1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingOPTIONS1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPATCH1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPOST1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPUT1() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

*/

}
