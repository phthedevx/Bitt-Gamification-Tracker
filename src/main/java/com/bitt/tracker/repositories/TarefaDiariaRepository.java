package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.TarefaDiaria;
import com.bitt.tracker.dto.TarefaDiariaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TarefaDiariaRepository extends JpaRepository<TarefaDiaria, Integer> {
    @Query("SELECT new com.bitt.tracker.dto.TarefaDiariaDTO(t.id, t.nome, t.categoria, t.pontos, COALESCE(h.concluido, false)) FROM TarefaDiaria t LEFT JOIN HistoricoTarefaDiaria h ON h.tarefa = t AND h.dataRegistro = :data")
    List<TarefaDiariaDTO> findTarefasComStatusNaData(@Param("data") LocalDate data);
}
