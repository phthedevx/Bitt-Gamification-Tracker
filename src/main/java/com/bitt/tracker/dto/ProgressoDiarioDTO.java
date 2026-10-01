package com.bitt.tracker.dto;
import java.time.LocalDate;
public record ProgressoDiarioDTO(LocalDate data, long dicasCurtidas, long receitasCurtidas, long metaDicas, long metaReceitas) {}
