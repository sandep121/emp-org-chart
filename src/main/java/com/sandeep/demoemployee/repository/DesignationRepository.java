package com.sandeep.demoemployee.repository;

import com.sandeep.demoemployee.entity.Designation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepository extends CrudRepository<Designation, Integer>
{
    Designation getByRoleLike(String str);
}
