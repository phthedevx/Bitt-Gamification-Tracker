package com.bitt.tracker.dto;
import com.bitt.tracker.domain.enums.TipoItem;
public record ItemResponseDTO(Integer id, String nome, TipoItem tipo, Boolean curtidoNoMes) {}
