package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.ApiResponse;
import com.project.spring_hibernate_jpa.dto.EmployeeRequest;
import com.project.spring_hibernate_jpa.dto.EmployeeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

public interface EmployeeService {

    void addEmployee(EmployeeRequest employeeRequest);

    EmployeeResponse getEmployeeById(Long id);

    ResponseEntity<ApiResponse> updateEmployee(Long id, @Valid EmployeeRequest employeeRequest);
}

