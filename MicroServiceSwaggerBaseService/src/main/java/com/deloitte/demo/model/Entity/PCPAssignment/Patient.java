package com.deloitte.demo.model.Entity.PCPAssignment;

/**
 * Created by yuntliu on 12/31/2017.
 */

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient implements Serializable {

    public Patient (Integer id, String Name, String PCPName)
    {
        setId(id);
        setName(Name);
        setPCPName(PCPName);
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
