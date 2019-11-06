package com.sandeep.demoemployee.entity;

import com.fasterxml.jackson.annotation.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
public class Employee{
    @Id
    @JsonProperty(value = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer empId;
    @Column
    @Nullable
    @JsonIgnore
    private Integer managerId;
    @Column
    @JsonProperty(value = "name")
    private String empName;
    @ManyToOne
    @JsonUnwrapped
    @JoinColumn(name = "DSGN_ID")
    private Designation designation;

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public Employee(@Nullable Integer managerId, String empName, Designation designation) {
        this.managerId = managerId;
        this.empName = empName;
        this.designation = designation;
    }

    public Employee(Employee emp) {
        this.empId = emp.getEmpId();
        this.managerId = emp.getManagerId();
        this.empName = emp.getEmpName();
        this.designation = emp.getDesignation();
    }



    public Employee() {
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
