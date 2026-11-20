package com.project.spring_hibernate_jpa.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank @Size(max=100) String name,
        @NotBlank @Email @Size(max=180) String email,
        @NotBlank @Size(max=80) String department,
        @NotNull @DecimalMin("0.0") @Digits(integer=10,fraction=2) BigDecimal salary) {}
