package com.bitt.tracker.api;
import com.bitt.tracker.dto.TarefaDiariaDTO;
import com.bitt.tracker.dto.TarefaDiariaToggleDTO;
import com.bitt.tracker.services.TarefaDiariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/tarefas-diarias")
@RequiredArgsConstructor
public class TarefaDiariaController {
    private final TarefaDiariaService tarefaService;
    @GetMapping
    public ResponseEntity<List<TarefaDiariaDTO>> listar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(tarefaService.listarComStatusDiario(data));
    }
    @PostMapping("/{id}/toggle-conclusao")
    public ResponseEntity<Void> toggleConclusao(@PathVariable Integer id, @RequestBody @Valid TarefaDiariaToggleDTO dto) {
        tarefaService.toggleConclusao(id, dto.dataRegistro());
        return ResponseEntity.ok().build();
    }
}
