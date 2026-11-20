CREATE TABLE employees (
 id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 email VARCHAR(180) NOT NULL,
 department VARCHAR(80) NOT NULL,
 salary DECIMAL(12,2) NOT NULL,
 version BIGINT NOT NULL DEFAULT 0,
 created_at DATETIME(6) NOT NULL,
 CONSTRAINT uk_employee_email UNIQUE (email),
 INDEX idx_employee_department (department),
 INDEX idx_employee_salary (salary)
);
