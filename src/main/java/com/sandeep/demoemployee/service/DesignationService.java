package com.sandeep.demoemployee.service;


import com.sandeep.demoemployee.entity.Designation;
import com.sandeep.demoemployee.repository.DesignationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DesignationService {
    private final DesignationRepository designationRepository;

    @Autowired
    public DesignationService(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }


    public Iterable<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }


    public String isValidEntry(Designation designation) {
        if(designation.getLvlId()<0)
        {
            return "Designation level cannot be -ve";
        }
        if(designationRepository.getByRoleLike(designation.getRole())!=null)
        {
            return "This designation already exists";
        }
        return null;
    }


    public Designation addDesignation(Designation designation) {
        designation.setRole(StringUtils.capitalize(designation.getRole()));
        designationRepository.save(designation);
        return designationRepository.findByDsgnId(designation.getDsgnId());
    }
}
