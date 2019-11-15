package com.sandeep.demoemployee.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

public class CrudeDesignation
{
    @JsonProperty(value = "jobTitle")
    private String role;
    private float lvlId;

    public CrudeDesignation(String role, float lvlId) {
        this.role = role;
        this.lvlId = lvlId;
    }

    public CrudeDesignation() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public float getLvlId() {
        return lvlId;
    }

    public void setLvlId(float lvlId) {
        this.lvlId = lvlId;
    }
}
