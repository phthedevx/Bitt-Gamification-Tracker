package com.bitt.tracker.api;

import com.bitt.tracker.domain.entities.TarefaDiaria;
import com.bitt.tracker.repositories.TarefaDiariaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TarefaDiariaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TarefaDiariaRepository tarefaRepository;

    @Test
    void deveRetornarTarefasNaoConcluidasParaNovaData() throws Exception {
        mockMvc.perform(get("/api/tarefas-diarias")
                .param("data", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11)))
                .andExpect(jsonPath("$[0].concluido").value(false));
    }

    @Test
    void deveFazerToggleDeTarefaComDataValida() throws Exception {
        List<TarefaDiaria> tarefas = tarefaRepository.findAll();
        Integer id = tarefas.get(0).getId();

        // 2. POST para concluir uma tarefa em: 2026-10-02
        String jsonBody = "{\"dataRegistro\": \"2026-10-02\"}";
        
        mockMvc.perform(post("/api/tarefas-diarias/{id}/toggle-conclusao", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        // 3. Depois do POST, GET em: 2026-10-02 deve retornar aquela tarefa concluída.
        // 7. Pontuação da tarefa deve continuar presente.
        mockMvc.perform(get("/api/tarefas-diarias")
                .param("data", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].concluido").value(true))
                .andExpect(jsonPath("$[?(@.id == " + id + ")].pontos").exists());

        // 4. GET em: 2026-10-03 deve retornar a mesma tarefa não concluída.
        mockMvc.perform(get("/api/tarefas-diarias")
                .param("data", "2026-10-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].concluido").value(false));

        // 5. Retornar para: 2026-10-02 deve continuar concluída.
        mockMvc.perform(get("/api/tarefas-diarias")
                .param("data", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].concluido").value(true));

        // 6. Toggle novamente deve restaurar o estado esperado
        mockMvc.perform(post("/api/tarefas-diarias/{id}/toggle-conclusao", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tarefas-diarias")
                .param("data", "2026-10-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].concluido").value(false));
    }
}
