package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.ApiResponse;
import com.project.spring_hibernate_jpa.dto.EmployeeRequest;
import com.project.spring_hibernate_jpa.dto.EmployeeResponse;
import com.project.spring_hibernate_jpa.exception.ConflictException;
import com.project.spring_hibernate_jpa.exception.ResourceNotFoundException;
import com.project.spring_hibernate_jpa.model.Employee;
import com.project.spring_hibernate_jpa.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

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
}