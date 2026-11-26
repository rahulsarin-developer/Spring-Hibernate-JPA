package com.project.spring_hibernate_jpa.dto;

import java.math.BigDecimal;

public record EmployeeFilter(String name, String department, BigDecimal minSalary, BigDecimal maxSalary) {}
