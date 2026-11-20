package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.EmployeeRequest;
import com.project.spring_hibernate_jpa.dto.EmployeeResponse;

public interface EmployeeService {

    void addEmployee(EmployeeRequest employeeRequest);

    EmployeeResponse getEmployeeById(Long id);
}

