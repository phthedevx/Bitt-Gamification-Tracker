package com.bitt.tracker.dto;
import com.bitt.tracker.domain.enums.CategoriaTarefa;
public record TarefaDiariaDTO(Integer id, String nome, CategoriaTarefa categoria, Boolean concluido) {}
