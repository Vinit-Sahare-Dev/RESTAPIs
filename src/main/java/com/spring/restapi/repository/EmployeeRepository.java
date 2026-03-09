package com.spring.restapi.repository;

import com.spring.restapi.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);
    List<Employee> findByGender(String gender);
    Optional<Employee> findByEmail(String email);
 
    List<Employee> findByDepartmentAndGender(String department, String gender);
    
    List<Employee> findBySalaryGreaterThan(Double minSalary);
    
    List<Employee> findBySalaryBetween(Double minSalary, Double maxSalary);
}