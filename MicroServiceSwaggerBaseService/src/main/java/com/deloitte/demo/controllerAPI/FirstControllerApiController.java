package com.deloitte.demo.controllerAPI;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-21T05:48:01.403Z")

//@RestController
@Controller
public class FirstControllerApiController implements FirstControllerApi {

    public ResponseEntity<String> rootUsingOPTIONS() {
        // do some magic!
        String result = null;

        result = "../FirstController/ GET = ResponseEntity<String>rootUsingGet\n" +
                "../FirstController/sayHello GET = ResponseEntity<String>sayHelloUsingGET\n" +
                "../FirstController/sayGoodBy GET = ResponseEntity<String>sayGoodByUsingGET\n" +
                "../FirstController/ PATCH = Function to update portion of the object resource (Not Exist) \n" +
                "../FirstController/ PUT = Function to Update the whole object resource (Not Exist) \n" +
                "../FirstController/ DELETE = Function to Delete the object resource (Not Exist) \n" +
                "../FirstController/ POST = Function to create object resource (Not Exist)\n" ;

        MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();

        header.set("Allow","GET,OPTIONS");
        header.set("Content-Type", "Text/Plain");

        return new ResponseEntity<String>(result, header, HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingGET() {
        // do some magic!
        return new ResponseEntity<String>("sayHelloUsingGET() return: This is teh sayHelloUsingGET function message",HttpStatus.OK);
    }


    public ResponseEntity<String> rootUsingGET() {
        // do some magic!
        System.out.println("Responding");


        return new ResponseEntity<String>( "rootUsingGet() return: This is the root message for FirstControllor" , HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingGET() {
        // do some magic!
        return new ResponseEntity<String>("sayGoodByUsingGET() return: This is the sayGoodByUsingGET function message",HttpStatus.OK);
    }


/*


    public ResponseEntity<String> rootUsingDELETE() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingHEAD() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }



    public ResponseEntity<String> rootUsingPATCH() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingPOST() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> rootUsingPUT() {
        // do some magic!
        return new ResponseEntity<String>("something here", HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingDELETE() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }



    public ResponseEntity<String> sayGoodByUsingHEAD() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingOPTIONS() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingPATCH() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingPOST() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayGoodByUsingPUT() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingDELETE() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }


    public ResponseEntity<String> sayHelloUsingHEAD() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingOPTIONS() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPATCH() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPOST() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public ResponseEntity<String> sayHelloUsingPUT() {
        // do some magic!
        return new ResponseEntity<String>(HttpStatus.OK);
    }
*/

}
