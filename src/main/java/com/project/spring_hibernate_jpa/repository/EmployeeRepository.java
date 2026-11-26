package com.project.spring_hibernate_jpa.repository;

import com.project.spring_hibernate_jpa.dto.EmployeeFilter;
import com.project.spring_hibernate_jpa.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    void saveEmployee(Employee employee);

    boolean existsByEmail(String email);

    boolean existsByEmail(String email, Long id);

    Optional<Employee> findById(Long id);

    void updateEmployee(Employee employee);

    void delete(Employee employee);

    List<Employee> searchRecord(EmployeeFilter filter, int page, int size, String sortBy, boolean ascending);

    long count(EmployeeFilter employeeFilter);

    Long countByDepartment(String department);
}
