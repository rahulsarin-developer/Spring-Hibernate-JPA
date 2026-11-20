package com.project.spring_hibernate_jpa.controller;

import com.project.spring_hibernate_jpa.dto.ApiResponse;
import com.project.spring_hibernate_jpa.dto.EmployeeRequest;
import com.project.spring_hibernate_jpa.dto.EmployeeResponse;
import com.project.spring_hibernate_jpa.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(path = "/addEmployee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> addEmployee(
            @Valid @RequestBody EmployeeRequest employeeRequest) {
        employeeService.addEmployee(employeeRequest);
        ApiResponse apiResponse = new ApiResponse(
                HttpStatus.CREATED.value(),
                "Employee Added Successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}