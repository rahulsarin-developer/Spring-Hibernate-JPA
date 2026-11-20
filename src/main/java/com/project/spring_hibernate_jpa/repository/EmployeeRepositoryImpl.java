package com.project.spring_hibernate_jpa.repository;

import com.project.spring_hibernate_jpa.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveEmployee(Employee employee) {
        if(employee.getId() == null) {
            entityManager.persist(employee);
            return;
        }
        entityManager.merge(employee);
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
"""
        SELECT COUNT(e) FROM Employee e WHERE LOWER(e.email) = LOWER(:email)
        """, Long.class).setParameter("email", email).getSingleResult();
        return count > 0;
    }
}
