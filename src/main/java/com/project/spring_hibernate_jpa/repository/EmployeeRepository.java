package com.project.spring_hibernate_jpa.repository;

import com.project.spring_hibernate_jpa.model.Employee;
import java.util.Optional;

public interface EmployeeRepository {

    void saveEmployee(Employee employee);

    boolean existsByEmail(String email);

    Optional<Employee> findById(Long id);
}
