package com.bitt.tracker.domain.entities;
import com.bitt.tracker.domain.enums.CategoriaTarefa;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "tb_tarefa_diaria")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TarefaDiaria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    @Enumerated(EnumType.STRING)
    private CategoriaTarefa categoria;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
}
