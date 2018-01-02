package com.deloitte.demo.execution.invocation;

import com.deloitte.demo.execution.invocation.PCPAssignment.PCPAssignmentOps;
import com.deloitte.demo.execution.operation.PatientOperation;

/**
 * Created by yuntliu on 12/31/2017.
 */
public class TestClient {


    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        PCPAssignmentOps ops = new PCPAssignmentOps();
        ops.loadAllPatient();

        PatientOperation patientOperationnew = new PatientOperation();
        patientOperationnew.PatientList = ops.PatientList;
        patientOperationnew.printPatientList();

        ops.updatePatient(new Integer(2), "Ethan");
        patientOperationnew.PatientList = ops.PatientList;
        patientOperationnew.printPatientList();

/*
        RestTemplate restTemplate = new RestTemplate();
        List<Patient> patientList = restTemplate.getForObject("http://localhost:8080/PCPAssignment/listAllPatient", new LinkedList<Patient>().getClass());
*/
/*

        Client c = Client.create();
        WebResource resource = c.resource("http://localhost:8090/PCPAssignment/listAllPatient");
        List<Patient> patientList = resource.get(new GenericType<List<Patient>>() {});
        PatientOperation patientOperation = new PatientOperation();
        patientOperation.PatientList = patientList;
        patientOperation.printPatientList();

        WebResource updateResource = c.resource("http://localhost:8090/PCPAssignment/update/3");
        List<Patient>  updatePatientList = updateResource.type("text/plain").put(new GenericType<LinkedList<Patient>>() {}, "Navneet Singh ");
        //  List<Patient>  updatePatientList = updateResource.type("application/json").put(new GenericType<LinkedList<Patient>>() {}, "Navneet Singh ");

        PatientOperation updatePatientOperation = new PatientOperation();
        updatePatientOperation.PatientList = updatePatientList;
        updatePatientOperation.printPatientList();
*/

    }

}
