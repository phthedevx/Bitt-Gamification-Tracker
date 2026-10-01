package com.bitt.tracker.api;
import com.bitt.tracker.dto.ProgressoDiarioDTO;
import com.bitt.tracker.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/progresso")
@RequiredArgsConstructor
public class ProgressoController {
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<ProgressoDiarioDTO> obterProgresso(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(itemService.calcularProgressoDiario(data));
    }
}
