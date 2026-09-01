package com.bitt.tracker.dto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record TarefaDiariaToggleDTO(@NotNull LocalDate dataRegistro) {}
