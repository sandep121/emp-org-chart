package com.sandeep.demoemployee.repository;

import com.sandeep.demoemployee.entity.Designation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DesignationRepository extends CrudRepository<Designation, Integer>
{
    Designation getByRoleLike(String str);
    Designation findByDsgnId(Integer id);

    @Query(value="SELECT MIN(lvl_Id) FROM DESIGNATION l where l.lvl_Id > :lvl", nativeQuery = true)
    Float nextLevel(@Param("lvl") Float lvl);

    @Query(value = "select MAX(lvl_id) FROM DESIGNATION", nativeQuery = true)
    Float maxLvlId();

}

