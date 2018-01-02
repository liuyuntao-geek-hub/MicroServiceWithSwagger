package com.deloitte.demo.model.ExternalEntity.PCPAssignment;

/**
 * Created by yuntliu on 12/31/2017.
 */

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient implements Serializable {

    public Patient (Integer id, String Name, String PCPName)
    {
        setId(id);
        setName(Name);
        setPcpname(PCPName);
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

    public String getPcpame() {
        return PCPName;
    }

    public void setPcpname(String pcpname) {
        this.PCPName = pcpname;
    }


    private Integer id;

    private String Name;

    private String PCPName;

}
