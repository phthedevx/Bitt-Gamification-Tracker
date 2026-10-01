package com.bitt.tracker.services;

import com.bitt.tracker.domain.entities.CurtidaMensal;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.domain.enums.TipoItem;
import com.bitt.tracker.dto.ProgressoDiarioDTO;
import com.bitt.tracker.repositories.CurtidaMensalRepository;
import com.bitt.tracker.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CurtidaMensalRepository curtidaMensalRepository;

    private Item dica1;
    private Item dica2;
    private Item receita1;

    @BeforeEach
    void setUp() {
        curtidaMensalRepository.deleteAll();
        itemRepository.deleteAll();
        dica1 = itemRepository.save(Item.builder().nome("Dica 1").tipo(TipoItem.DICA).ativo(true).dataCriacao(LocalDateTime.now()).build());
        dica2 = itemRepository.save(Item.builder().nome("Dica 2").tipo(TipoItem.DICA).ativo(true).dataCriacao(LocalDateTime.now()).build());
        receita1 = itemRepository.save(Item.builder().nome("Receita 1").tipo(TipoItem.RECEITA).ativo(true).dataCriacao(LocalDateTime.now()).build());
    }

    @Test
    void testPrimeiraCurtidaDoMes() {
        LocalDate data = LocalDate.of(2026, 9, 1);
        itemService.curtir(dica1.getId(), "2026-09", data);

        CurtidaMensal curtida = curtidaMensalRepository.findByItemAndAnoMes(dica1, "2026-09").orElseThrow();
        assertTrue(curtida.getCurtido());
        assertEquals(data, curtida.getDataCurtida());
    }

    @Test
    void testCurtidaRepetidaNoMesmoDia() {
        LocalDate data = LocalDate.of(2026, 9, 1);
        itemService.curtir(dica1.getId(), "2026-09", data);
        itemService.curtir(dica1.getId(), "2026-09", data); // repetida

        long count = curtidaMensalRepository.count();
        assertEquals(1, count); // Não cria novo registro
        
        ProgressoDiarioDTO progresso = itemService.calcularProgressoDiario(data);
        assertEquals(1, progresso.dicasCurtidas()); // Não aumenta progresso
    }

    @Test
    void testCurtidaRepetidaNoDiaSeguinte() {
        LocalDate data1 = LocalDate.of(2026, 9, 1);
        itemService.curtir(dica1.getId(), "2026-09", data1);
        
        LocalDate data2 = LocalDate.of(2026, 9, 2);
        itemService.curtir(dica1.getId(), "2026-09", data2); // tentativa no dia seguinte

        CurtidaMensal curtida = curtidaMensalRepository.findByItemAndAnoMes(dica1, "2026-09").orElseThrow();
        assertEquals(data1, curtida.getDataCurtida()); // Data original permanece
        
        ProgressoDiarioDTO progresso = itemService.calcularProgressoDiario(data2);
        assertEquals(0, progresso.dicasCurtidas()); // Progresso do novo dia não aumenta
    }

    @Test
    void testMesmaDicaEmOutraCompetencia() {
        LocalDate dataSetembro = LocalDate.of(2026, 9, 1);
        itemService.curtir(dica1.getId(), "2026-09", dataSetembro);
        
        LocalDate dataOutubro = LocalDate.of(2026, 10, 3);
        itemService.curtir(dica1.getId(), "2026-10", dataOutubro);

        assertEquals(2, curtidaMensalRepository.count());
        
        ProgressoDiarioDTO progressoOut = itemService.calcularProgressoDiario(dataOutubro);
        assertEquals(1, progressoOut.dicasCurtidas());
    }

    @Test
    void testDataIncompativel() {
        LocalDate data = LocalDate.of(2026, 10, 1);
        assertThrows(com.bitt.tracker.api.RegraDeNegocioException.class, () -> {
            itemService.curtir(dica1.getId(), "2026-09", data);
        });
    }

    @Test
    void testCalculoProgressoDiarioIndependente() {
        LocalDate data = LocalDate.of(2026, 9, 10);
        itemService.curtir(dica1.getId(), "2026-09", data);
        itemService.curtir(dica2.getId(), "2026-09", data);
        itemService.curtir(receita1.getId(), "2026-09", data);

        ProgressoDiarioDTO progresso = itemService.calcularProgressoDiario(data);
        assertEquals(2, progresso.dicasCurtidas());
        assertEquals(1, progresso.receitasCurtidas());
        assertEquals(25L, progresso.metaDicas());
    }
}
