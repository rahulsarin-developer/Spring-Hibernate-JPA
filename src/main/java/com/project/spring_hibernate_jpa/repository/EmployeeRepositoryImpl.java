package com.project.spring_hibernate_jpa.repository;

import com.project.spring_hibernate_jpa.dto.EmployeeFilter;
import com.project.spring_hibernate_jpa.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveEmployee(Employee employee) {
        if (employee.getId() == null) {
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

    @Override
    public boolean existsByEmail(String email, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(e) FROM Employee e WHERE LOWER(e.email) = LOWER(:email) " +
                                "AND e.id <> :id", Long.class)
                .setParameter("email", email)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Employee> findById(Long id) {

        return Optional.ofNullable(
                entityManager.find(Employee.class, id)
        );
    }

    @Override
    public void updateEmployee(Employee employee) {
        entityManager.merge(employee);
        entityManager.flush();
    }

    @Override
    public void delete(Employee employee) {
        entityManager.remove(
                entityManager.contains(employee) ? employee : entityManager.merge(employee)
        );
    }

    @Override
    public List<Employee> searchRecord(
            EmployeeFilter filter,
            int page,
            int size,
            String sortBy,
            boolean ascending) {
        String jpql = """
            SELECT e FROM Employee e
            WHERE (:name IS NULL OR LOWER(e.name) LIKE :name)
            AND (:department IS NULL OR e.department = :department)
            AND (:minSalary IS NULL OR e.salary >= :minSalary)
            AND (:maxSalary IS NULL OR e.salary <= :maxSalary)
            ORDER BY e.%s %s
            """.formatted(sortBy, ascending ? "ASC" : "DESC");
        TypedQuery<Employee> query =
                entityManager.createQuery(jpql, Employee.class);
        String name = filter.name() == null
                ? null
                : "%" + filter.name().toLowerCase() + "%";
        query.setParameter("name", name);
        query.setParameter("department", filter.department());
        query.setParameter("minSalary", filter.minSalary());
        query.setParameter("maxSalary", filter.maxSalary());
        List<Employee> employees = query
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        return employees;
    }

    @Override
    public long count(EmployeeFilter employeeFilter) {
        return 0;
    }

    @Override
    public Long countByDepartment(String department) {
        String jpql = """
        SELECT COUNT(e) FROM Employee e
        WHERE (:department IS NULL OR e.department = :department)
        """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("department", department)
                .getSingleResult();
    }
}