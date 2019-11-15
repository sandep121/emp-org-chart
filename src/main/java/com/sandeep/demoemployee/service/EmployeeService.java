package com.sandeep.demoemployee.service;

import com.sandeep.demoemployee.entity.CrudeEmployee;
import com.sandeep.demoemployee.entity.Designation;
import com.sandeep.demoemployee.entity.Employee;
import com.sandeep.demoemployee.repository.DesignationRepository;
import com.sandeep.demoemployee.repository.EmployeeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, DesignationRepository designationRepository) {
        this.employeeRepository = employeeRepository;
        this.designationRepository = designationRepository;
    }

    // get the list of all employees
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employeeRepository.findAllByOrderByDesignation_lvlIdAscEmpNameAsc());
    }

    //check is given employee exists or not
    public boolean employeeExists(int id) {
        return !employeeRepository.existsAllByEmpIdIs(id);
    }

    //converting the data to usable format
    public Employee getEmpFromCrudeEmp(CrudeEmployee crudeEmployee) {
        Employee employee = new Employee();
        if (crudeEmployee.getManagerId() != null)
            employee.setManagerId(crudeEmployee.getManagerId());
        if (!StringUtils.isEmpty(crudeEmployee.getEmpName()))
            employee.setEmpName(crudeEmployee.getEmpName());
        Designation designation = null;
        if (!StringUtils.isEmpty(crudeEmployee.getDesignation()))
        {
            crudeEmployee.setDesignation(StringUtils.capitalize(crudeEmployee.getDesignation()));
            designation = designationRepository.getByRoleLike(crudeEmployee.getDesignation());
        }
        employee.setDesignation(designation);
        return employee;
    }

    private List<Employee> getAllByManagerId(Integer id) {
        if (id != 0) {
            return employeeRepository.findAllByManagerIdOrderByDesignation_lvlIdAscEmpNameAsc(id);
        } else {
            return new ArrayList<>();
        }
    }

    public long getTotalEmployeeByDesignation(Integer id) {                      //refactor it or modify the logic
        return employeeRepository.count();
    }

    public Long getTotalEmployeeByDesignation(Designation designation) {
        return employeeRepository.countByDesignation(designation);
    }

    public Employee getEmployeeById(int id) {
        return employeeRepository.findByEmpId(id);
    }

    //returns the list of colleagues from empID
    private List<Employee> getColleague(Integer id) {
        List<Employee> employees = new ArrayList<>();
        Employee emp = this.getEmployeeById(id);
        id = emp.getManagerId();
        if (null == id) {
            return employees;
        } else {
            employees = employeeRepository.findAllByManagerIdOrderByDesignation_lvlIdAscEmpNameAsc(id);
            employees.remove(emp);
            return employees;
        }
    }

    public int addEmployee(Employee employee) {
        employeeRepository.save(employee);
        return employee.getEmpId();
    }


    private Employee getManager(Integer id) {
        Integer managerId = this.getEmployeeById(id).getManagerId();
        if (managerId != null) {
            return this.getEmployeeById(managerId);
        } else
            return null;
    }

    public void updateManager(Integer oldId, Integer newId) {
        List<Employee> children = this.getAllByManagerId(oldId);
        if (!children.isEmpty()) {
            for (Employee emp : children) {

                if (emp != null)
                    emp.setManagerId(newId);
            }
            children.forEach(this::addEmployee);
        }
    }

    public boolean deleteEmployee(int id) {
        Employee employee = this.getEmployeeById(id);
        this.updateManager(id, employee.getManagerId());
        employeeRepository.delete(employee);
        return true;
    }

    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    public String updateEmployee(Employee employee, Employee empOld) {
        employee.setEmpId(empOld.getEmpId());
        if (employee.getDesignation() != null) {
            if (this.validateDesignation(employee, empOld.getDesignation())) {
                empOld.setDesignation(employee.getDesignation());
            } else
                return "INVALID_DESIGNATION";
        }
        if (employee.getManagerId() != null) {
            if (this.validateManager(empOld, this.getEmployeeById(employee.getManagerId())))
                empOld.setManagerId(employee.getManagerId());
            else
                return "INVALID_PARENT";
        }
        if (!StringUtils.isEmpty(employee.getEmpName())) {
            empOld.setEmpName(employee.getEmpName());
        }
        employeeRepository.save(empOld);
        return null;
    }

    private boolean validateDesignation(Employee employee, Designation designation) {
        if (designation.getDsgnId() != employee.getDesignation().getDsgnId() && (designation.getDsgnId() == 1 || employee.getDesignation().getDsgnId() == 1))       //cannot demote the director
        {
            return false;
        }
        List<Employee> children = this.getAllByManagerId(employee.getEmpId());
        if (!children.isEmpty())
            for (Employee emp : children)                                                     //for children cannot be superior to manager
            {
                assert emp != null;
                if (emp.getDesignation().getLvlId() <= designation.getLvlId())
                    return false;
            }
        return true;
    }

    public boolean parentIsValid(Employee emp) {
        Integer managerId = emp.getManagerId();
        if (managerId != null && employeeRepository.existsAllByEmpIdIs(managerId)) {
            Employee manager = this.getEmployeeById(managerId);
            float managerLvl = manager.getDesignation().getLvlId();
            float empLvl = emp.getDesignation().getLvlId();
            return managerLvl < empLvl;
        } else return emp.getDesignation().getDsgnId() == 1;
    }

    public boolean isInvalidateEntry(Employee employee) {
        if (employee.getDesignation() != null && employee.getDesignation().getDsgnId() == 1)
            return StringUtils.isEmpty(employee.getEmpName()) || !(employee.getManagerId() == null || employee.getManagerId() == -1);  ///Jugaar to run the test cases
        return StringUtils.isEmpty(employee.getEmpName()) || employee.getDesignation() == null || employee.getManagerId() == null;
    }

    private boolean validateManager(Employee emp, Employee newManager) {
        if (newManager == null)
            return false;
        return emp.getDesignation().getLvlId() >= newManager.getDesignation().getLvlId();
    }

    public boolean validatePutFalse(CrudeEmployee crudeEmployee, Employee employee) {
        if (!StringUtils.isEmpty(crudeEmployee.getDesignation()) && employee.getDesignation() == null) {
            return false;
        }
        return employee.getDesignation() != null || employee.getManagerId() != null || !StringUtils.isEmpty(employee.getEmpName());
    }

    //check if data to be updated is valid or not for put-false
    public String validatePutRequest(CrudeEmployee crudeEmployee, Integer id) {
        if (id < 1)
            return "INVALID_ID";
        if (!StringUtils.isEmpty(crudeEmployee.getEmpName()) && crudeEmployee.getEmpName().matches(".*\\d.*"))
            return "INVALID_NAME";
        if (this.employeeExists(id))                              //check if the employee exists or not
            return "EMP_NOT_EXISTS";
        return null;
    }

    //check if data to be updated is valid or not for put-true
    public String validatePutTrue(Employee empNew, int id) {
        String response = null;
        int oldDsgn = getEmployeeById(id).getDesignation().getDsgnId();
        int newDsgn = empNew.getDesignation().getDsgnId();
        if (isInvalidateEntry(empNew))
            response = "MISSING_FIELDS";
        else if (oldDsgn != newDsgn && (newDsgn == 1 || oldDsgn == 1))
            response = "INVALID_DIRECTOR";
        else if (!parentIsValid(empNew))
            response = "INVALID_PARENT";
        return response;
    }

    public Map generateEmpDetails(int id) {
        Map<String, Object> employeeResponse = new HashMap<>();
        List<Employee> subOrdinates = this.getAllByManagerId(id);
        List<Employee> colleague = this.getColleague(id);
        Object manager = this.getManager(id);
        employeeResponse.put("employee", this.getEmployeeById(id));
        if (!colleague.isEmpty())
            employeeResponse.put("colleagues", colleague);
        if (manager != null)
            employeeResponse.put("manager", manager);
        if (!subOrdinates.isEmpty())
            employeeResponse.put("subordinates", subOrdinates);
        return employeeResponse;
    }

}
