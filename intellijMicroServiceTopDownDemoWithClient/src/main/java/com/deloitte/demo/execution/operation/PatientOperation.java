package com.deloitte.demo.execution.operation;

import com.deloitte.demo.model.ExternalEntity.PCPAssignment.Patient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuntliu on 12/31/2017.
 */
public class PatientOperation {
    public List<Patient> PatientList = new LinkedList<Patient>();

    public void printPatientList ()
    {
        Iterator<Patient> iterator = PatientList.iterator();

        while (iterator.hasNext())
        {
            Patient patient = iterator.next();
            System.out.println("===================================");
            System.out.println("Patient ID: "+patient.getId());
            System.out.println("Patient Name: "+patient.getName());
            System.out.println("Patient PCPName: "+patient.getPcpame());
        }
    }


}