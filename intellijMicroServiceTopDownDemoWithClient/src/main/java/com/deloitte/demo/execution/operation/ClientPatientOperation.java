package com.deloitte.demo.execution.operation;

import com.deloitte.demo.model.Entity.PCPClient.ClientPatient;
import com.deloitte.demo.model.ExternalEntity.PCPAssignment.Patient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuntliu on 12/31/2017.
 */
public class ClientPatientOperation {


    public List<ClientPatient> loadClientPatientList(List<Patient> patientList)
    {
        List<ClientPatient> clientPatientList = new LinkedList<ClientPatient>();

        Iterator<Patient> iterator = patientList.iterator();

        while (iterator.hasNext())
        {
            Patient patient = iterator.next();
            clientPatientList.add(new ClientPatient(patient));
        }

        return clientPatientList;
    }




}
