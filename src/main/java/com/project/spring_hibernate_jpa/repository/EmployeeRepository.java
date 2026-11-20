package com.project.spring_hibernate_jpa.repository;

import com.project.spring_hibernate_jpa.model.Employee;

public interface EmployeeRepository {

    void saveEmployee(Employee employee);

    boolean existsByEmail(String email);
}
