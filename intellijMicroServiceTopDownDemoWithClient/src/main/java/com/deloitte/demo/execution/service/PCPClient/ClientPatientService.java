package com.deloitte.demo.execution.service.PCPClient;

import com.deloitte.demo.execution.invocation.PCPAssignment.PCPAssignmentOps;
import com.deloitte.demo.model.Entity.PCPClient.ClientPatient;

import java.util.LinkedList;
import java.util.List;
import com.deloitte.demo.execution.operation.*;
import org.springframework.stereotype.Service;

/**
 * Created by yuntliu on 12/31/2017.
 */

@Service
public class ClientPatientService {

    public List<ClientPatient> ClientPatientList = new LinkedList<ClientPatient>();

    public void loadClientPatient()
    {
        PCPAssignmentOps ops = new PCPAssignmentOps();
        ops.loadAllPatient();
        ClientPatientList = new ClientPatientOperation().loadClientPatientList(ops.PatientList);
    }
    public List<ClientPatient> listAllPatient()
    {
        return ClientPatientList;
    }

    public void updatePCP (Integer id, String name)
    {
        ClientPatientList.get(id.intValue()-1).setPCPName(name);
    }


}
