package com.bitt.tracker.services;
import com.bitt.tracker.domain.entities.TarefaDiaria;
import com.bitt.tracker.dto.TarefaDiariaDTO;
import com.bitt.tracker.repositories.HistoricoTarefaDiariaRepository;
import com.bitt.tracker.repositories.TarefaDiariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TarefaDiariaService {
    private final TarefaDiariaRepository tarefaRepository;
    private final HistoricoTarefaDiariaRepository historicoRepository;
    public List<TarefaDiariaDTO> listarComStatusDiario(LocalDate data) {
        return tarefaRepository.findAll().stream().map(t -> {
            boolean concluido = historicoRepository.findByTarefaAndDataRegistro(t, data).map(h -> Boolean.TRUE.equals(h.getConcluido())).orElse(false);
            return new TarefaDiariaDTO(t.getId(), t.getNome(), t.getCategoria(), concluido);
        }).toList();
    }
}
