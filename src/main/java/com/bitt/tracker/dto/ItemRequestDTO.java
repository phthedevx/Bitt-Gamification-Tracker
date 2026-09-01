package com.bitt.tracker.dto;
import com.bitt.tracker.domain.enums.TipoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ItemRequestDTO(@NotBlank String nome, @NotNull TipoItem tipo) {}
