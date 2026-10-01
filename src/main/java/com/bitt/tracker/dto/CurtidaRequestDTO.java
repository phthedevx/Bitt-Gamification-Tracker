package com.bitt.tracker.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CurtidaRequestDTO(
        @NotBlank String anoMes,
        @NotNull LocalDate data
) {}
