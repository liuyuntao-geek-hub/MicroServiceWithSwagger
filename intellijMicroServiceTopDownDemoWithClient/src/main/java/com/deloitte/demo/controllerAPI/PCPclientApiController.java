package com.deloitte.demo.controllerAPI;

//import com.deloitte.demo.modelEntity.Iterable;

import com.deloitte.demo.execution.service.PCPClient.ClientPatientService;
//import com.deloitte.demo.modelEntity.Iterable;
import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-01T05:00:30.883Z")

@Controller
public class PCPclientApiController implements PCPclientApi {

    private ClientPatientService clientPatientService;

    @Autowired
    public void setClientPatientService(ClientPatientService clientPatientService) {
        clientPatientService.loadClientPatient();
        this.clientPatientService = clientPatientService;
    }



    public ResponseEntity<Iterable> listEveryPatientUsingGET() {
        // do some magic!
        clientPatientService.loadClientPatient();
        Iterable clientPatients = (Iterable) clientPatientService.listAllPatient();

        return new ResponseEntity<Iterable>(clientPatients, HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingGETUsingGET() {
        // do some magic!

        return new ResponseEntity<String>("Testing", HttpStatus.OK);
    }

    public ResponseEntity<Iterable> updateProductUsingPUT(@ApiParam(value = "id",required=true ) @PathVariable("id") Integer id,
        @ApiParam(value = "PCPName" ,required=true )  @Valid @RequestBody String pcPName) {
        // do some magic!
        return new ResponseEntity<Iterable>(HttpStatus.OK);
    }

}
