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

    @Test
    void testRegressaoBugCurtidaMensalEDiaria() {
        // Cenario: Dica inicialmente nao curtida
        String anoMes = "2026-10";
        LocalDate data = LocalDate.of(2026, 10, 1);
        
        // Registrar curtida
        itemService.curtir(dica1.getId(), anoMes, data);
        
        // 1. Mensal e Diaria concordam
        var itens = itemService.listarComStatus(TipoItem.DICA, anoMes);
        var dicaAtualizada = itens.stream().filter(i -> i.id().equals(dica1.getId())).findFirst().orElseThrow();
        assertTrue(dicaAtualizada.curtido(), "A dica deve estar curtida no catalogo mensal");
        
        var progresso = itemService.calcularProgressoDiario(data);
        assertEquals(1, progresso.dicasCurtidas(), "O progresso diario deve contabilizar a curtida");
        
        // 2. Teste do dia seguinte
        LocalDate dataSeguinte = LocalDate.of(2026, 10, 2);
        var itensDiaSeguinte = itemService.listarComStatus(TipoItem.DICA, anoMes);
        var dicaDiaSeguinte = itensDiaSeguinte.stream().filter(i -> i.id().equals(dica1.getId())).findFirst().orElseThrow();
        assertTrue(dicaDiaSeguinte.curtido(), "A dica deve continuar curtida no mes");
        
        var progressoSeguinte = itemService.calcularProgressoDiario(dataSeguinte);
        assertEquals(0, progressoSeguinte.dicasCurtidas(), "O progresso do dia seguinte nao deve contabilizar");
        
        // 3. Teste da nova competencia
        String anoMesNovo = "2026-11";
        var itensMesNovo = itemService.listarComStatus(TipoItem.DICA, anoMesNovo);
        var dicaMesNovo = itensMesNovo.stream().filter(i -> i.id().equals(dica1.getId())).findFirst().orElseThrow();
        assertFalse(dicaMesNovo.curtido(), "Na nova competencia, a dica nao deve estar curtida");
    }
}

