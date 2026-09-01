package com.bitt.tracker.services;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.dto.ItemRequestDTO;
import com.bitt.tracker.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    public void salvarLote(List<ItemRequestDTO> dtos) {
        dtos.forEach(this::salvar);
    }
    public Item salvar(ItemRequestDTO dto) {
        Item item = Item.builder().nome(dto.nome()).tipo(dto.tipo()).ativo(true).dataCriacao(LocalDateTime.now()).build();
        return itemRepository.save(item);
    }
}
