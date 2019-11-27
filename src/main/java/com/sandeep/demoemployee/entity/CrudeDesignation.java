package com.sandeep.demoemployee.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

public class CrudeDesignation
{
    @JsonProperty(value = "jobTitle")
    private String role;
    private String reportsTo;
    private boolean parallel;

    public CrudeDesignation(String role, String reportsTo) {
        this.role = role;
        this.reportsTo = reportsTo;
    }

    public CrudeDesignation(String role, String reportsTo, boolean parallel) {
        this.role = role;
        this.reportsTo = reportsTo;
        this.parallel = parallel;
    }

    public CrudeDesignation() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReportsTo() {
        return reportsTo;
    }

    public boolean parallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public void setReportsTo(String reportsTo) {
        this.reportsTo = reportsTo;
    }
}
