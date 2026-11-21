package com.project.spring_hibernate_jpa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalaryRequest(@NotNull @DecimalMin("0.0") @Digits(integer=10,fraction=2) BigDecimal salary) {}
