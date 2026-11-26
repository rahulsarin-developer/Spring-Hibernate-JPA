package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.*;
import com.project.spring_hibernate_jpa.exception.ConflictException;
import com.project.spring_hibernate_jpa.exception.ResourceNotFoundException;
import com.project.spring_hibernate_jpa.model.Employee;
import com.project.spring_hibernate_jpa.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "email", "department", "salary", "createdAt");

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public void addEmployee(EmployeeRequest employeeRequest) {

        if (employeeRepository.existsByEmail(employeeRequest.email())) {
            throw new ConflictException(
                    "Employee already exists with email: " + employeeRequest.email()
            );
        }

        Employee employee = new Employee();
        employee.setName(employeeRequest.name());
        employee.setEmail(employeeRequest.email());
        employee.setDepartment(employeeRequest.department());
        employee.setSalary(employeeRequest.salary());
        employeeRepository.saveEmployee(employee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        Employee employee = optionalEmployee.orElseThrow(
                ()->new ResourceNotFoundException("Employee not found with id: " + id));
        EmployeeResponse response = new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getVersion(),
                employee.getCreatedAt()
        );
        return response;
    }

    @Override
    public ResponseEntity<ApiResponse> updateEmployee(Long id, EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id
                ));
        if (employeeRepository.existsByEmail(employeeRequest.email(), id)) {
            throw new ConflictException(
                    "Employee already exists with email: " + employeeRequest.email()
            );
        }
        employee.setName(employeeRequest.name());
        employee.setEmail(employeeRequest.email());
        employee.setDepartment(employeeRequest.department());
        employee.setSalary(employeeRequest.salary());
        employeeRepository.updateEmployee(employee);
        ApiResponse response = new ApiResponse(
                200, "Employee Updated Successfully"
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(
                "Employee not found with id: " + id
        ));
        employeeRepository.delete(employee);
        ApiResponse response = new ApiResponse(
                200,
                "Employee Deleted Successfully"
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> updateSalary(Long id, SalaryRequest salaryRequest) {
        Employee employee = employeeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(
                "Employee not found with id: " + id));
        employee.setSalary(salaryRequest.salary());
        employeeRepository.updateEmployee(employee);
        ApiResponse response = new ApiResponse(
                200,
                "Employee Salary Updated Successfully"
        );
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> searchRecord(
            EmployeeFilter employeeFilter,
            int page,
            int size,
            String sortBy,
            String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be 1..100");
        }
        if (!SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field");
        }
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "direction must be asc or desc");
        }
        if (employeeFilter.minSalary() != null
                && employeeFilter.maxSalary() != null
                && employeeFilter.minSalary()
                .compareTo(employeeFilter.maxSalary()) > 0) {
            throw new IllegalArgumentException(
                    "minSalary must be <= maxSalary");
        }
        List<Employee> employees = employeeRepository.searchRecord(
                employeeFilter,
                page,
                size,
                sortBy,
                direction.equalsIgnoreCase("asc")
        );
        List<EmployeeResponse> employeeResponses = new ArrayList<>();
        for (Employee employee : employees) {
            EmployeeResponse response = new EmployeeResponse(
                    employee.getId(),
                    employee.getName(),
                    employee.getEmail(),
                    employee.getDepartment(),
                    employee.getSalary(),
                    employee.getVersion(),
                    employee.getCreatedAt()
            );
            employeeResponses.add(response);
        }
        long count = employeeRepository.count(employeeFilter);
        int totalPages = (int) Math.ceil((double) count / size);
        PageResponse<EmployeeResponse> pageResponse = new PageResponse<>(
                employeeResponses,
                page,
                size,
                count,
                totalPages
        );
        return pageResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(String department) {
        return employeeRepository.countByDepartment(department);
    }
}