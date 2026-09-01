package com.bitt.tracker.services;
import com.bitt.tracker.domain.entities.CurtidaMensal;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.domain.enums.TipoItem;
import com.bitt.tracker.dto.ItemRequestDTO;
import com.bitt.tracker.dto.ItemResponseDTO;
import com.bitt.tracker.repositories.CurtidaMensalRepository;
import com.bitt.tracker.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CurtidaMensalRepository curtidaMensalRepository;
    public void salvarLote(List<ItemRequestDTO> dtos) { dtos.forEach(this::salvar); }
    public Item salvar(ItemRequestDTO dto) {
        return itemRepository.save(Item.builder().nome(dto.nome()).tipo(dto.tipo()).ativo(true).dataCriacao(LocalDateTime.now()).build());
    }
    public List<ItemResponseDTO> listarComStatus(TipoItem tipo, String anoMes) {
        return itemRepository.findByTipoWithCurtidas(tipo, anoMes).stream().map(i -> {
            boolean curtido = i.getCurtidas() != null && i.getCurtidas().stream().anyMatch(c -> c.getAnoMes().equals(anoMes) && Boolean.TRUE.equals(c.getCurtido()));
            return new ItemResponseDTO(i.getId(), i.getNome(), i.getTipo(), curtido);
        }).toList();
    }
    @Transactional
    public void toggleCurtida(Integer itemId, String anoMes) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado"));
        CurtidaMensal curtida = curtidaMensalRepository.findByItemAndAnoMes(item, anoMes).orElseGet(() -> CurtidaMensal.builder().item(item).anoMes(anoMes).curtido(false).build());
        curtida.setCurtido(!Boolean.TRUE.equals(curtida.getCurtido()));
        curtida.setDataAtualizacao(LocalDateTime.now());
        curtidaMensalRepository.save(curtida);
    }
}
