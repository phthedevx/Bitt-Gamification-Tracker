package com.bitt.tracker.domain.entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "tb_curtida_mensal")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CurtidaMensal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne @JoinColumn(name = "item_id")
    private Item item;
    private String anoMes;
    private Boolean curtido;
    private LocalDate dataCurtida;
    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
}
