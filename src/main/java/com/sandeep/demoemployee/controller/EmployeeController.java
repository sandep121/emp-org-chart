package com.sandeep.demoemployee.controller;

import com.sandeep.demoemployee.entity.CrudeEmployee;
import com.sandeep.demoemployee.entity.Employee;
import com.sandeep.demoemployee.entity.NewEmployee;
import com.sandeep.demoemployee.service.EmployeeService;
import com.sandeep.demoemployee.util.Messages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/employees")
@Api
public class EmployeeController {
    private final EmployeeService employeeService;
    private final Messages messages;

    @Autowired
    public EmployeeController(EmployeeService employeeService, Messages messages) {
        this.employeeService = employeeService;
        this.messages = messages;
    }

    @ApiOperation(value = "View a list of available employees", response = List.class)
    @GetMapping
    public ResponseEntity getAllEmployee() {
        List<Employee> allEmployees = new ArrayList<>(employeeService.getAllEmployees());
        return new ResponseEntity<>(allEmployees, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity findAllByEmpId(@PathVariable int id) {
        if (id < 1) {
            return new ResponseEntity<>(messages.getMessage("INVALID_ID"), HttpStatus.BAD_REQUEST);
        }
        if (employeeService.employeeExists(id))
            return new ResponseEntity<>(messages.getMessage("EMP_NOT_EXISTS"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(employeeService.generateEmpDetails(id), HttpStatus.OK);
    }

    @GetMapping("/CountDsgId={id}")                                 //used for debugging
    public ResponseEntity countByDesignationId(@PathVariable int id) {
        return new ResponseEntity<>(employeeService.getTotalEmployeeByDesignation(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity addEmployee(@RequestBody CrudeEmployee crudeEmployee) {
        //crude is needed for designation field
        if (crudeEmployee.getEmpName() == null || crudeEmployee.getEmpName().matches(".*\\d.*"))
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        Employee employee = employeeService.getEmpFromCrudeEmp(crudeEmployee);
        if (employeeService.validateEntry(employee))
            return new ResponseEntity<>(messages.getMessage("MISSING_FIELDS"), HttpStatus.BAD_REQUEST);
        boolean isValidParent = employeeService.parentIsValid(employee);
        if (employee.getDesignation().getDsgnId() == 1 && employeeService.getTotalEmployeeByDesignation(1) != 0) {
            return new ResponseEntity<>(messages.getMessage("DIRECTOR_EXISTS"), HttpStatus.BAD_REQUEST);
        } else if (!isValidParent) {
            return new ResponseEntity<>(messages.getMessage("INVALID_PARENT"), HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>(employeeService.getEmployeeById(employeeService.addEmployee(employee)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEmployee(@RequestBody NewEmployee crudeEmployee, @PathVariable int id) {
        if (id < 1)
            return new ResponseEntity<>(messages.getMessage("INVALID_ID"),HttpStatus.BAD_REQUEST);
        String response = employeeService.validatePut(crudeEmployee, id);
        if (response != null)
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        Employee empOld = employeeService.getEmpFromCrudeEmp(crudeEmployee);
        if (crudeEmployee.isReplace()) {
            Employee empNew = new Employee(empOld);
            if (empNew.getManagerId() == null)
                empNew.setManagerId(employeeService.getEmployeeById(id).getManagerId());
            response = employeeService.validatePutTrue(empNew, id);
            if (response != null)                    //to check if all the required fields are available
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            empNew.setEmpId(employeeService.addEmployee(empNew));
            employeeService.updateManager(id, empNew.getEmpId());
            employeeService.deleteEmployee(id);
            return new ResponseEntity<>(employeeService.generateEmpDetails(empNew.getEmpId()), HttpStatus.OK);
        } else if (employeeService.validatePutFalse(crudeEmployee, empOld)) {
            String result;
            result = employeeService.updateEmployee(empOld, employeeService.getEmployeeById(id));
            if (result == null)
                return new ResponseEntity<>(employeeService.generateEmpDetails(id), HttpStatus.OK);
            else
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>(messages.getMessage("NOTHING_TO_UPDATE"), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        if (id < 1)
            return new ResponseEntity<>(messages.getMessage("INVALID_ID"),HttpStatus.BAD_REQUEST);
        if (employeeService.employeeExists(id)) {
            return new ResponseEntity<>(messages.getMessage("EMP_NOT_EXISTS"), HttpStatus.NOT_FOUND);
        } else if (employeeService.getEmployeeById(id).getDesignation().getDsgnId() == 1 && employeeService.getTotalEmployeeCount() != 1) {

            return new ResponseEntity<>(messages.getMessage("UNABLE_TO_DELETE_DIRECTOR"), HttpStatus.BAD_REQUEST);
        } else if (employeeService.deleteEmployee(id)) {
            return new ResponseEntity<>(messages.getMessage("DELETED"), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>("Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}