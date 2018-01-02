package com.deloitte.demo.execution.service.PCPAssignment;

/**
 * Created by yuntliu on 12/31/2017.
 */

import java.util.LinkedList;
import java.util.List;

import com.deloitte.demo.model.Entity.PCPAssignment.Patient;
import org.springframework.stereotype.Service;


/**
 * Created by yuntliu on 12/17/2017.
 */

@Service
public class PatientService {

    public List<Patient> PatientList = new LinkedList<Patient>();

    public void initPatient()
    {
        Patient one = new Patient(1, "Yuntao Liu", "");
        Patient two = new Patient(2, "Maulik", "");
        Patient three = new Patient(3, "Brian", "");
        Patient four = new Patient(4, "Pattabhi", "");
        Patient five = new Patient(5, "Rajkumar", "");
        Patient six = new Patient(6, "Surekha", "");
        PatientList.add(one);
        PatientList.add(two);
        PatientList.add(three);
        PatientList.add(four);
        PatientList.add(five);
        PatientList.add(six);
    }


    public List<Patient> listAllPatient()
    {
        return PatientList;
    }

    public void updatePCP (Integer id, String name)
    {
        PatientList.get(id.intValue()-1).setPCPName(name);
    }


}
