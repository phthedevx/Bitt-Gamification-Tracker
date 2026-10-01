package com.bitt.tracker.services;

import com.bitt.tracker.domain.entities.TarefaDiaria;
import com.bitt.tracker.domain.enums.CategoriaTarefa;
import com.bitt.tracker.dto.TarefaDiariaDTO;
import com.bitt.tracker.repositories.HistoricoTarefaDiariaRepository;
import com.bitt.tracker.repositories.TarefaDiariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TarefaDiariaServiceTest {

    @Autowired
    private TarefaDiariaService tarefaService;

    @Autowired
    private TarefaDiariaRepository tarefaRepository;

    @Autowired
    private HistoricoTarefaDiariaRepository historicoRepository;

    @BeforeEach
    void setUp() {
        historicoRepository.deleteAll();
        // The migrations already insert the 11 tasks, but let's test using the inserted data.
    }

    @Test
    void testExistemExatamente11TarefasBase() {
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        assertEquals(11, tarefas.size(), "Devem existir 11 tarefas-base cadastradas");
    }

    @Test
    void testPontuacaoTotalDe390() {
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        int totalPontos = tarefas.stream().mapToInt(TarefaDiaria::getPontos).sum();
        assertEquals(390, totalPontos, "O total de pontos deve ser 390");
    }

    @Test
    void testBuscaDataSemHistoricoRetornaNaoConcluidas() {
        LocalDate data = LocalDate.of(2026, 10, 1);
        List<TarefaDiariaDTO> dtos = tarefaService.listarComStatusDiario(data);
        
        assertEquals(11, dtos.size());
        assertTrue(dtos.stream().noneMatch(TarefaDiariaDTO::concluido), "Nenhuma tarefa deve estar concluída inicialmente");
    }

    @Test
    void testConcluiTarefaEmUmaData() {
        LocalDate data = LocalDate.of(2026, 10, 1);
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        Integer idTarefa = tarefas.get(0).getId();

        tarefaService.toggleConclusao(idTarefa, data);

        List<TarefaDiariaDTO> dtos = tarefaService.listarComStatusDiario(data);
        TarefaDiariaDTO dto = dtos.stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow();
        
        assertTrue(dto.concluido(), "A tarefa deve estar concluída");
    }

    @Test
    void testConclusaoNaoAfetaOutraData() {
        LocalDate data1 = LocalDate.of(2026, 10, 1);
        LocalDate data2 = LocalDate.of(2026, 10, 2);
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        Integer idTarefa = tarefas.get(0).getId();

        tarefaService.toggleConclusao(idTarefa, data1);

        List<TarefaDiariaDTO> dtos1 = tarefaService.listarComStatusDiario(data1);
        TarefaDiariaDTO dto1 = dtos1.stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow();
        assertTrue(dto1.concluido(), "A tarefa deve estar concluída na data 1");

        List<TarefaDiariaDTO> dtos2 = tarefaService.listarComStatusDiario(data2);
        TarefaDiariaDTO dto2 = dtos2.stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow();
        assertFalse(dto2.concluido(), "A tarefa NÃO deve estar concluída na data 2");
    }

    @Test
    void testMesmaTarefaPodeSerConcluidaEmDiasDiferentes() {
        LocalDate data1 = LocalDate.of(2026, 10, 1);
        LocalDate data2 = LocalDate.of(2026, 10, 2);
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        Integer idTarefa = tarefas.get(0).getId();

        tarefaService.toggleConclusao(idTarefa, data1);
        tarefaService.toggleConclusao(idTarefa, data2);

        List<TarefaDiariaDTO> dtos1 = tarefaService.listarComStatusDiario(data1);
        List<TarefaDiariaDTO> dtos2 = tarefaService.listarComStatusDiario(data2);

        assertTrue(dtos1.stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow().concluido());
        assertTrue(dtos2.stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow().concluido());
    }

    @Test
    void testMarcarEDesmarcarTarefa() {
        LocalDate data = LocalDate.of(2026, 10, 1);
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        Integer idTarefa = tarefas.get(0).getId();

        // Marca
        tarefaService.toggleConclusao(idTarefa, data);
        assertTrue(tarefaService.listarComStatusDiario(data).stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow().concluido());

        // Desmarca
        tarefaService.toggleConclusao(idTarefa, data);
        assertFalse(tarefaService.listarComStatusDiario(data).stream().filter(t -> t.id().equals(idTarefa)).findFirst().orElseThrow().concluido());
    }
}
