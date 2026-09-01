package com.bitt.tracker.api;
import com.bitt.tracker.dto.ProgressoMensalDTO;
import com.bitt.tracker.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/progresso")
@RequiredArgsConstructor
public class ProgressoController {
    private final ItemService itemService;
    @GetMapping("/{anoMes}")
    public ResponseEntity<ProgressoMensalDTO> obterProgresso(@PathVariable String anoMes) {
        return ResponseEntity.ok(itemService.calcularProgressoMensal(anoMes));
    }
}
