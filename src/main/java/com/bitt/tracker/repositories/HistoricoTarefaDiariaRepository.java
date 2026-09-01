package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.HistoricoTarefaDiaria;
import com.bitt.tracker.domain.entities.TarefaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
public interface HistoricoTarefaDiariaRepository extends JpaRepository<HistoricoTarefaDiaria, Integer> {
    Optional<HistoricoTarefaDiaria> findByTarefaAndDataRegistro(TarefaDiaria tarefa, LocalDate dataRegistro);
}
