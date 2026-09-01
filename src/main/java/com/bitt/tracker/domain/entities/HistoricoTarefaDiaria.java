package com.bitt.tracker.domain.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "tb_historico_tarefa_diaria")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoricoTarefaDiaria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne @JoinColumn(name = "tarefa_id")
    private TarefaDiaria tarefa;
    private LocalDate dataRegistro;
    private Boolean concluido;
    private LocalDateTime dataAtualizacao;
}
