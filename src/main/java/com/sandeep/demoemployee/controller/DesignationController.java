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
import java.util.*;

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
    public ResponseEntity getAllDesignations() {
        return new ResponseEntity<>(designationService.getAllDesignations(), HttpStatus.OK);
    }
    @GetMapping("/")
    public ResponseEntity getDetailedDesignation(){                                         //used for debugging
        List<Designation> allDesignation=new ArrayList((Collection) designationService.getAllDesignations());
        Map<String, String> dsgn = new HashMap<String, String>();
        allDesignation.forEach(
                designation -> {
                    dsgn.put(designation.getRole(),designation.getLvlId().toString());
                }
                );
        return new ResponseEntity<>(dsgn, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity addEmployee(@RequestBody CrudeDesignation crudeDesignation) {
        //crude is needed for reportsTo field
        crudeDesignation.setRole(StringUtils.capitalize(crudeDesignation.getRole()));
        crudeDesignation.setReportsTo(StringUtils.capitalize(crudeDesignation.getReportsTo()));
        String validation=designationService.isInvalidateEntry(crudeDesignation);
        if(validation!=null)
        {
            return new ResponseEntity<>(validation,HttpStatus.BAD_REQUEST);
        }
            Designation designation=designationService.getDesignationFromCrudeDsgn(crudeDesignation);
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
