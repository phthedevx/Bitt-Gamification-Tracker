package com.bitt.tracker.domain.entities;
import com.bitt.tracker.domain.enums.TipoItem;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "tb_item")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    @Enumerated(EnumType.STRING)
    private TipoItem tipo;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CurtidaMensal> curtidas;
}
