package com.sandeep.demoemployee.service;


import com.sandeep.demoemployee.entity.CrudeDesignation;
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

    public Designation addDesignation(Designation designation) {
        designationRepository.save(designation);
        return designationRepository.findByDsgnId(designation.getDsgnId());
    }

    public Designation jobExists(Designation designation) {
        return designationRepository.getByRoleLike(designation.getRole());

    }

    public boolean deleteDesignation(Designation designation) {
        designationRepository.delete(designation);
        return designationRepository.getByRoleLike(designation.getRole()) == null;
    }

    public Designation getDesignationFromCrudeDsgn(CrudeDesignation crudeDesignation) {
        //if(StringUtils.isEmpty(c))
        Designation senior=designationRepository.getByRoleLike(crudeDesignation.getReportsTo());
        Float nextLvl=designationRepository.nextLevel(senior.getLvlId());
        if(nextLvl==null)
        {
            nextLvl=designationRepository.maxLvlId()+1;
            crudeDesignation.setParallel(true);
        }

        Designation newDesignation= new Designation();
        newDesignation.setRole(crudeDesignation.getRole());
        if(crudeDesignation.parallel())
            newDesignation.setLvlId(nextLvl);
        else
            newDesignation.setLvlId((nextLvl+senior.getLvlId())/2);
        return newDesignation;
    }

    public String isInvalidateEntry(CrudeDesignation crudeDesignation) {

        if(designationRepository.getByRoleLike(crudeDesignation.getReportsTo())==null && designationRepository.count()!=0)
        {
            return "invalid senior";
        }
        crudeDesignation.setRole(StringUtils.capitalize(crudeDesignation.getRole()));
        if(designationRepository.getByRoleLike(crudeDesignation.getRole())!=null)
        {
            return "Designation already Exists";
        }



        return null;
    }
}
