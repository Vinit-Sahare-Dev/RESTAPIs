package com.spring.restapi.service;

import com.spring.restapi.models.Employee;
import com.spring.restapi.repository.EmployeeRepository;
import com.spring.restapi.exception.EmployeeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(@Valid Employee employee) {
        logger.info("SAVING EMPLOYEE - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        calculateEmployeeDeductions(employee);
        Employee saved = employeeRepository.save(employee);
        
        logger.info("EMPLOYEE SAVED SUCCESSFULLY - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                   saved.getId(), saved.getName(), saved.getDepartment(), saved.getGender(), 
                   saved.getSalary(), saved.getBonus(), saved.getPf(), saved.getTax());
        return saved;
    }

    public void deleteEmployeeById(Long id) {
        // First get employee details before deleting
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("EMPLOYEE NOT FOUND FOR DELETION - ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });
        
        logger.info("DELETING EMPLOYEE - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        employeeRepository.delete(employee);
        
        logger.info("EMPLOYEE DELETED SUCCESSFULLY - ID: {}, Name: {}, Department: {}", 
                   id, employee.getName(), employee.getDepartment());
    }

    private void calculateEmployeeDeductions(Employee employee) {
        logger.debug("CALCULATING DEDUCTIONS - Employee: {} (ID: {})", employee.getName(), employee.getId());
        Double salary = employee.getSalary();
        Double bonus = salary * 0.10;
        employee.setBonus(bonus);
        Double pf = salary * 0.12;
        employee.setPf(pf);
        Double tax = calculateTax(salary);
        employee.setTax(tax);
        logger.debug("DEDUCTIONS CALCULATED - Employee: {}, Bonus: {}, PF: {}, Tax: {}, Net Salary: {}", 
                   employee.getName(), bonus, pf, tax, (salary + bonus - pf - tax));
    }

    private Double calculateTax(Double salary) {
        if (salary <= 250000) {
            return 0.0;
        } else if (salary <= 500000) {
            return (salary - 250000) * 0.05;
        } else if (salary <= 1000000) {
            return 12500 + (salary - 500000) * 0.20;
        } else {
            return 112500 + (salary - 1000000) * 0.30;
        }
    }

    public List<Employee> getAllEmployees() {
        logger.info("FETCHING ALL EMPLOYEES");
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(this::calculateEmployeeDeductions);
        
        logger.info("FETCHED {} EMPLOYEES:", employees.size());
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                       emp.getId(), emp.getName(), emp.getDepartment(), emp.getGender(), 
                       emp.getSalary(), emp.getBonus(), emp.getPf(), emp.getTax());
        }
        return employees;
    }

    public Optional<Employee> getEmployeeById(Long id) {
        logger.info("FETCHING EMPLOYEE BY ID: {}", id);
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            logger.warn("EMPLOYEE NOT FOUND - ID: {}", id);
        } else {
            Employee employee = employeeOpt.get();
            calculateEmployeeDeductions(employee);
            logger.info("EMPLOYEE FOUND - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                       employee.getId(), employee.getName(), employee.getDepartment(), employee.getGender(), 
                       employee.getSalary(), employee.getBonus(), employee.getPf(), employee.getTax());
        }
        return employeeOpt;
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        logger.info("FETCHING EMPLOYEES BY DEPARTMENT: {}", department);
        List<Employee> employees = employeeRepository.findByDepartment(department);
        employees.forEach(this::calculateEmployeeDeductions);
        
        logger.info("FETCHED {} EMPLOYEES FROM DEPARTMENT {}:", employees.size(), department);
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                       emp.getId(), emp.getName(), emp.getGender(), emp.getSalary(), 
                       emp.getBonus(), emp.getPf(), emp.getTax());
        }
        return employees;
    }

    public List<Employee> getEmployeesByGender(String gender) {
        logger.info("FETCHING EMPLOYEES BY GENDER: {}", gender);
        List<Employee> employees = employeeRepository.findByGender(gender);
        employees.forEach(this::calculateEmployeeDeductions);
        
        logger.info("FETCHED {} EMPLOYEES WITH GENDER {}:", employees.size(), gender);
        for (Employee emp : employees) {
            logger.info("  - ID: {}, Name: {}, Department: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                       emp.getId(), emp.getName(), emp.getDepartment(), emp.getSalary(), 
                       emp.getBonus(), emp.getPf(), emp.getTax());
        }
        return employees;
    }

    public Employee updateEmployee(Long id, @Valid Employee employeeDetails) {
        logger.info("UPDATING EMPLOYEE - ID: {}, New Details - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employeeDetails.getName(), employeeDetails.getDepartment(), 
                   employeeDetails.getGender(), employeeDetails.getSalary());
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("EMPLOYEE NOT FOUND FOR UPDATE - ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });
        
        // Log old values
        logger.info("BEFORE UPDATE - ID: {}, Old Details - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        employee.setName(employeeDetails.getName());
        employee.setSalary(employeeDetails.getSalary());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setGender(employeeDetails.getGender());
        calculateEmployeeDeductions(employee);
        
        Employee updated = employeeRepository.save(employee);
        
        logger.info("EMPLOYEE UPDATED SUCCESSFULLY - ID: {}, Name: {}, Department: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                   updated.getId(), updated.getName(), updated.getDepartment(), updated.getGender(), 
                   updated.getSalary(), updated.getBonus(), updated.getPf(), updated.getTax());
        return updated;
    }
    
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    

    public List<Employee> findByDepartmentAndGender(String department, String gender) {
        return employeeRepository.findByDepartmentAndGender(department, gender);
    }
    
    public List<Employee> findBySalaryGreaterThan(Double minSalary) {
        return employeeRepository.findBySalaryGreaterThan(minSalary);
    }
    
    public List<Employee> findBySalaryBetween(Double minSalary, Double maxSalary) {
        return employeeRepository.findBySalaryBetween(minSalary, maxSalary);
    }

    public List<Employee> saveAllEmployees(List<@Valid Employee> employees) {
        logger.info("BULK SAVING {} EMPLOYEES:", employees.size());
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            logger.info("  EMPLOYEE {} - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                       i + 1, emp.getName(), emp.getDepartment(), emp.getGender(), emp.getSalary());
        }
        
        employees.forEach(this::calculateEmployeeDeductions);
        List<Employee> saved = employeeRepository.saveAll(employees);
        
        logger.info("BULK SAVE COMPLETED - {} EMPLOYEES SAVED SUCCESSFULLY", saved.size());
        return saved;
    }

    public int getEmployeeCount() {
        logger.info("FETCHING EMPLOYEE COUNT");
        int count = (int) employeeRepository.count();
        logger.info("TOTAL EMPLOYEE COUNT: {}", count);
        return count;
    }

    public void deleteAllEmployees() {
        logger.info("DELETING ALL EMPLOYEES");
        long count = employeeRepository.count();
        employeeRepository.deleteAll();
        logger.info("ALL {} EMPLOYEES DELETED SUCCESSFULLY", count);
    }

    public Employee partialUpdateEmployee(Long id, Map<String, Object> updates) {
        logger.info("PARTIAL UPDATE EMPLOYEE - ID: {}, Updates: {}", id, updates);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("EMPLOYEE NOT FOUND FOR PARTIAL UPDATE - ID: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });
        
        // Log current state before update
        logger.info("BEFORE PARTIAL UPDATE - ID: {}, Current - Name: {}, Department: {}, Gender: {}, Salary: {}", 
                   id, employee.getName(), employee.getDepartment(), employee.getGender(), employee.getSalary());
        
        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> {
                    employee.setName((String) value);
                    logger.info("UPDATING NAME TO: {}", value);
                }
                case "salary" -> {
                    employee.setSalary(Double.valueOf(value.toString()));
                    logger.info("UPDATING SALARY TO: {}", value);
                    calculateEmployeeDeductions(employee);
                }
                case "department" -> {
                    employee.setDepartment((String) value);
                    logger.info("UPDATING DEPARTMENT TO: {}", value);
                }
                case "gender" -> {
                    employee.setGender((String) value);
                    logger.info("UPDATING GENDER TO: {}", value);
                }
                default -> {
                    logger.error("INVALID FIELD UPDATE ATTEMPT - Field: '{}', Value: {}, Employee ID: {}", key, value, id);
                    throw new IllegalArgumentException("Field '" + key + "' is not updatable.");
                }
            }
        });
        
        Employee saved = employeeRepository.save(employee);
        
        logger.info("PARTIAL UPDATE SUCCESSFUL - ID: {}, Final - Name: {}, Department: {}, Gender: {}, Salary: {}, Bonus: {}, PF: {}, Tax: {}", 
                   saved.getId(), saved.getName(), saved.getDepartment(), saved.getGender(), 
                   saved.getSalary(), saved.getBonus(), saved.getPf(), saved.getTax());
        return saved;
    }
}