package com.sandeep.demoemployee.controller;


import com.sandeep.demoemployee.entity.CrudeDesignation;
import com.sandeep.demoemployee.entity.Designation;
import com.sandeep.demoemployee.service.DesignationService;
import com.sandeep.demoemployee.util.Messages;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/designation")
@Api
public class DesignationController {

    private final DesignationService designationService;
    private final Messages messages;

    @Autowired
    public DesignationController(DesignationService designationService, Messages messages) {
        this.designationService = designationService;
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

        //        Employee employee = designationService.getEmpFromCrudeEmp(designation);
//        if (designationService.isInvalidateEntry(employee))
//            return new ResponseEntity<>(messages.getMessage("MISSING_FIELDS"), HttpStatus.BAD_REQUEST);
//        boolean isValidParent = designationService.parentIsValid(employee);
//        if (employee.getDesignation().getDsgnId() == 1 && designationService.getTotalEmployeeByDesignation(1) != 0) {
//            return new ResponseEntity<>(messages.getMessage("DIRECTOR_EXISTS"), HttpStatus.BAD_REQUEST);
//        } else if (!isValidParent) {
//            return new ResponseEntity<>(messages.getMessage("INVALID_PARENT"), HttpStatus.BAD_REQUEST);
//        } else
//            return new ResponseEntity<>(designationService.getEmployeeById(designationService.addEmployee(employee)), HttpStatus.CREATED);
    }

}
