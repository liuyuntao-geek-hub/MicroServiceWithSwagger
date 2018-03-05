package com.anthem.smartpcp.drools.model;

/**
 * Created by khushbu on 2/21/2018.
 */
public class State {

    private String longname;
    private String shortname;

    public void setLongname(String longname) {
        this.longname = longname;
    }

    public String getLongname() {
        return longname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getShortname() {
        return shortname;

    }
}