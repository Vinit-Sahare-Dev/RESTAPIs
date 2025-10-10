package com.spring.restapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "NAME", nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @NotNull(message = "Salary must not be null")
    @Min(value = 0, message = "Salary must be positive")
    @Column(name = "SALARY", nullable = false)
    private Double salary;

    @NotBlank(message = "Department is mandatory")
    @Column(name = "DEPARTMENT", nullable = false)
    private String department;

    @NotBlank(message = "Gender is mandatory")
    @Column(name = "GENDER", nullable = false)
    private String gender;

    @JsonIgnore
    @Min(value = 0, message = "Bonus must be positive")
    @Column(name = "BONUS")
    private Double bonus;

    @JsonIgnore
    @Min(value = 0, message = "PF must be positive")
    @Column(name = "PF")
    private Double pf;

    @JsonIgnore
    @Min(value = 0, message = "Tax must be positive")
    @Column(name = "TAX")
    private Double tax;

    public Employee() {}

    public Employee(String name, String email, Double salary, String department, String gender) {
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.department = department;
        this.gender = gender;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getBonus() { return bonus; }
    public void setBonus(Double bonus) { this.bonus = bonus; }

    public Double getPf() { return pf; }
    public void setPf(Double pf) { this.pf = pf; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }
}