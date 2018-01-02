package com.deloitte.demo.model.Entity.PCPClient;

import com.deloitte.demo.model.ExternalEntity.PCPAssignment.Patient;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by yuntliu on 12/31/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientPatient {


    public ClientPatient  (Integer id, String Name, String PCPName)
    {
        setId(id);
        setName(Name);
        setPCPName(PCPName);
        setRegistertime();
    }
    public ClientPatient (Patient patient)
    {
        setId(patient.getId());
        setName(patient.getName());
        setPCPName(patient.getPcpame());
        setRegistertime();
    }

    public String registertime;

    public void setRegistertime()
    {
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Calendar now = Calendar.getInstance();
        this.registertime = sdf.format(now.getTime());
    }

    public  String getRegistertime()
    {
        return this.registertime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPCPName() {
        return PCPName;
    }

    public void setPCPName(String PCPName) {
        this.PCPName = PCPName;
    }

    @ApiModelProperty(notes = "The Patient ID")
    private Integer id;
    @ApiModelProperty(notes = "Patient Name")
    private String Name;
    @ApiModelProperty(notes = "PCP name")
    private String PCPName;
}
