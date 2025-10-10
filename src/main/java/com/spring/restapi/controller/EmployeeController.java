package com.spring.restapi.controller;


import com.spring.restapi.models.Employee;
import com.spring.restapi.service.EmployeeService;
import com.spring.restapi.exception.EmployeeNotFoundException;
import com.spring.restapi.exception.IllegalDepartmentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;


    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        logger.info("CREATE EMPLOYEE REQUEST - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        Employee savedEmployee = employeeService.saveEmployee(employee);
        
        logger.info("EMPLOYEE CREATED - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   savedEmployee.getId(), savedEmployee.getName(), savedEmployee.getDepartment(), 
                   savedEmployee.getGender(), savedEmployee.getSalary());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/employees/" + savedEmployee.getId())
                .body(savedEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employeeDetails) {
        logger.info("UPDATE EMPLOYEE REQUEST - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employeeDetails.getName(), employeeDetails.getDepartment(), 
                   employeeDetails.getGender(), employeeDetails.getSalary());
        
        Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
        
        logger.info("EMPLOYEE UPDATED - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, updatedEmployee.getName(), updatedEmployee.getDepartment(), 
                   updatedEmployee.getGender(), updatedEmployee.getSalary());
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Employee> partialUpdateEmployee(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        logger.info("PARTIAL UPDATE EMPLOYEE REQUEST - ID: {}, Updates: {}", id, updates);
        Employee updatedEmployee = employeeService.partialUpdateEmployee(id, updates);
        
        logger.info("EMPLOYEE PARTIALLY UPDATED - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, updatedEmployee.getName(), updatedEmployee.getDepartment(), 
                   updatedEmployee.getGender(), updatedEmployee.getSalary());
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Long id) {
        // First get employee details before deleting
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        
        logger.info("DELETE EMPLOYEE REQUEST - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        employeeService.deleteEmployeeById(id);
        
        logger.info("EMPLOYEE DELETED - ID: {}, Name: {}, Department: {}", 
                   id, employee.getName(), employee.getDepartment());
        return ResponseEntity.ok("Employee with id " + id + " deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("GET ALL EMPLOYEES REQUEST");
        List<Employee> employees = employeeService.getAllEmployees();
        
        // Log summary of all employees
        logger.info("RETURNING {} EMPLOYEES:", employees.size());
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                       emp.getId(), emp.getName(), emp.getDepartment(), emp.getGender(), emp.getSalary());
        }
        
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        logger.info("GET EMPLOYEE BY ID REQUEST - ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> {
                    logger.warn("EMPLOYEE NOT FOUND - ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });
        
        logger.info("EMPLOYEE FOUND - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   employee.getId(), employee.getName(), employee.getDepartment(), 
                   employee.getGender(), employee.getSalary());
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        logger.info("GET EMPLOYEES BY DEPARTMENT REQUEST - Department: {}", department);
        List<String> validDepartments = List.of("IT", "HR", "Finance");

        if (!validDepartments.contains(department)) {
            logger.error("INVALID DEPARTMENT - Department: {}", department);
            throw new IllegalDepartmentException("Department " + department + " is not allowed.");
        }

        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        
        logger.info("RETURNING {} EMPLOYEES FROM DEPARTMENT {}:", employees.size(), department);
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Gender: {}, Salary: {}", 
                       emp.getId(), emp.getName(), emp.getGender(), emp.getSalary());
        }
        
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<Employee>> getEmployeesByGender(@PathVariable String gender) {
        logger.info("GET EMPLOYEES BY GENDER REQUEST - Gender: {}", gender);
        List<Employee> employees = employeeService.getEmployeesByGender(gender);
        
        logger.info("RETURNING {} EMPLOYEES WITH GENDER {}:", employees.size(), gender);
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Department: {}, Salary: {}", 
                       emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary());
        }
        
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Employee>> saveAllEmployees(@RequestBody List<@Valid Employee> employees) {
        logger.info("BULK SAVE EMPLOYEES REQUEST - Count: {}", employees.size());
        
        // Log details of all employees being saved
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            logger.info("EMPLOYEE {} - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                       i + 1, emp.getName(), emp.getDepartment(), emp.getGender(), emp.getSalary());
        }
        
        List<Employee> savedEmployees = employeeService.saveAllEmployees(employees);
        
        logger.info("BULK SAVE SUCCESSFUL - Total Employees Saved: {}", savedEmployees.size());
        return ResponseEntity.ok(savedEmployees);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getEmployeeCount() {
        logger.info("GET EMPLOYEE COUNT REQUEST");
        int count = employeeService.getEmployeeCount();
        logger.info("EMPLOYEE COUNT: {}", count);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllEmployees() {
        logger.info("DELETE ALL EMPLOYEES REQUEST");
        int count = employeeService.getEmployeeCount(); // Get count before deletion
        employeeService.deleteAllEmployees();
        logger.info("ALL EMPLOYEES DELETED - Total Deleted: {}", count);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam("email") String email) {
        return employeeService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/department-gender")
    public List<Employee> getEmployeesByDepartmentAndGender(
            @RequestParam String department, 
            @RequestParam String gender) {
        return employeeService.findByDepartmentAndGender(department, gender);
    }

    @GetMapping("/salary-greater-than")
    public List<Employee> getEmployeesBySalaryGreaterThan(@RequestParam Double minSalary) {
        return employeeService.findBySalaryGreaterThan(minSalary);
    }

    @GetMapping("/salary-between")
    public List<Employee> getEmployeesBySalaryBetween(
            @RequestParam Double minSalary, 
            @RequestParam Double maxSalary) {
        return employeeService.findBySalaryBetween(minSalary, maxSalary);
    }
}