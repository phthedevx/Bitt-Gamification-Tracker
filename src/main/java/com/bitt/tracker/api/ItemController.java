package com.bitt.tracker.api;
import com.bitt.tracker.domain.enums.TipoItem;
import com.bitt.tracker.dto.CurtidaToggleDTO;
import com.bitt.tracker.dto.ItemBatchImportDTO;
import com.bitt.tracker.dto.ItemResponseDTO;
import com.bitt.tracker.services.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/itens")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    @PostMapping("/batch")
    public ResponseEntity<Void> importBatch(@RequestBody @Valid ItemBatchImportDTO dto) {
        itemService.salvarLote(dto.itens());
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> listar(@RequestParam TipoItem tipo, @RequestParam String anoMes) {
        return ResponseEntity.ok(itemService.listarComStatus(tipo, anoMes));
    }
    @PostMapping("/{id}/toggle-curtida")
    public ResponseEntity<Void> toggleCurtida(@PathVariable Integer id, @RequestBody @Valid CurtidaToggleDTO dto) {
        itemService.toggleCurtida(id, dto.anoMes());
        return ResponseEntity.ok().build();
    }
}
