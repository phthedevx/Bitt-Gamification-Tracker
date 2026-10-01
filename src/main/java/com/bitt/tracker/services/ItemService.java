package com.bitt.tracker.services;
import com.bitt.tracker.domain.entities.CurtidaMensal;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.domain.enums.TipoItem;
import com.bitt.tracker.dto.ItemRequestDTO;
import com.bitt.tracker.dto.ItemResponseDTO;
import com.bitt.tracker.dto.ProgressoMensalDTO;
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
        public void salvarLote(List<ItemRequestDTO> dtos) {
        List<Item> itens = dtos.stream().map(dto -> Item.builder().nome(dto.nome()).tipo(dto.tipo()).ativo(true).dataCriacao(LocalDateTime.now()).build()).toList();
        itemRepository.saveAll(itens);
    }
    public Item salvar(ItemRequestDTO dto) { return itemRepository.save(Item.builder().nome(dto.nome()).tipo(dto.tipo()).ativo(true).dataCriacao(LocalDateTime.now()).build()); }
        public List<ItemResponseDTO> listarComStatus(TipoItem tipo, String anoMes) {
        return itemRepository.findItensComStatusCurtida(tipo, anoMes).stream().map(obj -> {
            Item i = (Item) obj[0];
            boolean curtido = (Boolean) obj[1];
            return new ItemResponseDTO(i.getId(), i.getNome(), i.getTipo(), curtido);
        }).toList();
    }
    @Transactional
    public void curtir(Integer itemId, String anoMes, java.time.LocalDate data) {
        String mesDaData = String.format("%d-%02d", data.getYear(), data.getMonthValue());
        if (!mesDaData.equals(anoMes)) {
            throw new com.bitt.tracker.api.RegraDeNegocioException("Data incompatível com a competência informada");
        }
        
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado"));
        CurtidaMensal curtida = curtidaMensalRepository.findByItemAndAnoMes(item, anoMes).orElse(null);
        
        if (curtida == null) {
            curtida = CurtidaMensal.builder()
                .item(item)
                .anoMes(anoMes)
                .curtido(true)
                .dataCurtida(data)
                .build();
            curtidaMensalRepository.save(curtida);
        } else {
            if (!Boolean.TRUE.equals(curtida.getCurtido())) {
                 curtida.setCurtido(true);
                 if (curtida.getDataCurtida() == null) {
                     curtida.setDataCurtida(data);
                 }
                 curtida.setDataAtualizacao(LocalDateTime.now());
                 curtidaMensalRepository.save(curtida);
            }
        }
    }
    public ProgressoMensalDTO calcularProgressoMensal(String anoMes) {
        List<Object[]> resultados = curtidaMensalRepository.countCurtidasAgrupadasPorTipo(anoMes);
        long dicasCurtidas = 0L;
        long receitasCurtidas = 0L;

        for (Object[] resultado : resultados) {
            TipoItem tipo = (TipoItem) resultado[0];
            long count = ((Number) resultado[1]).longValue();
            if (tipo == TipoItem.DICA) {
                dicasCurtidas = count;
            } else if (tipo == TipoItem.RECEITA) {
                receitasCurtidas = count;
            }
        }

        return new ProgressoMensalDTO(anoMes, dicasCurtidas, receitasCurtidas, 25L, 25L);
    }
}
