package com.bitt.tracker.services;
import com.bitt.tracker.domain.entities.HistoricoTarefaDiaria;
import com.bitt.tracker.domain.entities.TarefaDiaria;
import com.bitt.tracker.dto.TarefaDiariaDTO;
import com.bitt.tracker.repositories.HistoricoTarefaDiariaRepository;
import com.bitt.tracker.repositories.TarefaDiariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TarefaDiariaService {
    private final TarefaDiariaRepository tarefaRepository;
    private final HistoricoTarefaDiariaRepository historicoRepository;
        public List<TarefaDiariaDTO> listarComStatusDiario(LocalDate data) {
        return tarefaRepository.findTarefasComStatusNaData(data);
    }
    @Transactional
    public void toggleConclusao(Integer tarefaId, LocalDate data) {
        TarefaDiaria tarefa = tarefaRepository.findById(tarefaId).orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"));
        HistoricoTarefaDiaria historico = historicoRepository.findByTarefaAndDataRegistro(tarefa, data)
            .orElseGet(() -> HistoricoTarefaDiaria.builder().tarefa(tarefa).dataRegistro(data).concluido(false).build());
        historico.setConcluido(!Boolean.TRUE.equals(historico.getConcluido()));
        historico.setDataAtualizacao(LocalDateTime.now());
        historicoRepository.save(historico);
    }
}
