package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.EmployeeRequest;
import com.project.spring_hibernate_jpa.exception.ConflictException;
import com.project.spring_hibernate_jpa.model.Employee;
import com.project.spring_hibernate_jpa.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
}
