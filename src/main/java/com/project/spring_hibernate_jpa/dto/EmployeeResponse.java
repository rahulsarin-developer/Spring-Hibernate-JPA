package com.project.spring_hibernate_jpa.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        String department,
        BigDecimal salary,
        long version,
        Instant createdAt) {}
