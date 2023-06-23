package com.example.springboot.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRecordDto(@NotBlank(message = "Passa o nome viado") String name, @NotNull BigDecimal value) {
}