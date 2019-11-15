package com.sandeep.demoemployee.controller;


import com.sandeep.demoemployee.entity.CrudeDesignation;
import com.sandeep.demoemployee.entity.Designation;
import com.sandeep.demoemployee.service.DesignationService;
import com.sandeep.demoemployee.service.EmployeeService;
import com.sandeep.demoemployee.util.Messages;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("/designation")
@Api
public class DesignationController {

    private final EmployeeService employeeService;
    private final DesignationService designationService;
    private final Messages messages;

    @Autowired
    public DesignationController(DesignationService designationService, EmployeeService employeeService, Messages messages) {
        this.designationService = designationService;
        this.employeeService = employeeService;
        this.messages = messages;
    }

    @GetMapping
    public ResponseEntity getAllEmployee() {
        return new ResponseEntity<>(designationService.getAllDesignations(), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity addEmployee(@RequestBody CrudeDesignation crudeDesignation) {
        //crude is needed for LvlId field
        Designation designation=new Designation(crudeDesignation.getRole(), crudeDesignation.getLvlId());
        if (StringUtils.isEmpty(designation.getRole()) || designation.getRole().matches(".*\\d.*"))
            return new ResponseEntity<>("Please Enter a valid designation", HttpStatus.BAD_REQUEST);

        String validateEntry=designationService.isValidEntry(designation);
        if(validateEntry != null)
            return new ResponseEntity<>(validateEntry, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(designationService.addDesignation(designation), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteEmployee(@RequestBody Designation dsgn)
    {
        Designation designation=designationService.jobExists(dsgn);
        if(designation==null)
        {
            return new ResponseEntity<>(MessageFormat.format(messages.getMessage("DSGN_NOT_FOUND"),dsgn.getRole()), HttpStatus.BAD_REQUEST);
        }
        if(employeeService.getTotalEmployeeByDesignation(designation)!=0)
        {
            return new ResponseEntity<>(MessageFormat.format(messages.getMessage("EMP_EXISTS"),dsgn.getRole()), HttpStatus.BAD_REQUEST);
        }
        if(designationService.deleteDesignation(designation))
            return new ResponseEntity<>(MessageFormat.format(messages.getMessage("DSGN_DELETED"),designation.getRole()), HttpStatus.NO_CONTENT);

        return new ResponseEntity<>("Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
//        if (employeeService.employeeExists(id)) {
//            return new ResponseEntity<>(MessageFormat.format(messages.getMessage("EMP_NOT_EXISTS"),id), HttpStatus.NOT_FOUND);
//        } else if (employeeService.getEmployeeById(id).getDesignation().getDsgnId() == 1 && employeeService.getTotalEmployeeCount() != 1) {
//
//            return new ResponseEntity<>(messages.getMessage("UNABLE_TO_DELETE_DIRECTOR"), HttpStatus.BAD_REQUEST);
//        } else if (employeeService.deleteEmployee(id)) {
//            return new ResponseEntity<>(messages.getMessage("DELETED"), HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>("Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
