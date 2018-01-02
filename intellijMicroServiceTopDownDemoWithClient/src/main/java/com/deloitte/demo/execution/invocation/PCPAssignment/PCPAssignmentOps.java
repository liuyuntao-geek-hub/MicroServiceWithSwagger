package com.deloitte.demo.execution.invocation.PCPAssignment;

import java.util.LinkedList;
import java.util.List;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.*;
import com.deloitte.demo.model.ExternalEntity.PCPAssignment.Patient;


/**
 * Created by yuntliu on 12/31/2017.
 */
public class PCPAssignmentOps {

    public List<Patient> PatientList = new LinkedList<Patient>();

    public void loadAllPatient()
    {
        Client c = Client.create();
        WebResource resource = c.resource("http://localhost:8090/PCPAssignment/listAllPatient");
        PatientList = resource.get(new GenericType<List<Patient>>() {});
    }

    public void updatePatient (Integer id, String PCPName)
    {
        Client c = Client.create();
        WebResource updateResource = c.resource("http://localhost:8090/PCPAssignment/update/"+ id.toString());
        PatientList = updateResource.type("text/plain").put(new GenericType<LinkedList<Patient>>() {}, PCPName);

    }


}
