package com.sandeep.demoemployee.repository;

import com.sandeep.demoemployee.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer>
{
    List<Employee> findAllByOrderByDesignation_lvlIdAscEmpNameAsc();
    boolean existsAllByEmpIdIs(int id);
    List<Employee> findAllByManagerIdOrderByDesignation_lvlIdAscEmpNameAsc(int id);
    Employee findByEmpId(Integer id);
}




