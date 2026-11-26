package com.project.spring_hibernate_jpa.controller;

import com.project.spring_hibernate_jpa.dto.*;
import com.project.spring_hibernate_jpa.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> addEmployee(
            @Valid @RequestBody EmployeeRequest employeeRequest) {
        employeeService.addEmployee(employeeRequest);
        ApiResponse apiResponse = new ApiResponse(
                HttpStatus.CREATED.value(),
                "Employee Added Successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/fetch/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest employeeRequest) {
        return employeeService.updateEmployee(id, employeeRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    @PatchMapping("/update/{id}/salary")
    public ResponseEntity<ApiResponse> updateSalary(
            @PathVariable Long id,
            @Valid @RequestBody SalaryRequest salaryRequest) {
        return employeeService.updateSalary(id, salaryRequest);
    }

    @GetMapping("/search")
    public PageResponse<EmployeeResponse> searchRecord(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return employeeService.searchRecord(
                new EmployeeFilter(name, department, minSalary, maxSalary), page, size, sortBy, direction
        );
    }

    @GetMapping("/count")
    public Map<String, Long> count(@RequestParam(required = false) String department) {
        return Map.of("count", employeeService.count(department));
    }
}
