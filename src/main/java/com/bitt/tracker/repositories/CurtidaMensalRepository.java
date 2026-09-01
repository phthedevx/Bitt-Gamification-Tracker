package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.CurtidaMensal;
import com.bitt.tracker.domain.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CurtidaMensalRepository extends JpaRepository<CurtidaMensal, Integer> {
    Optional<CurtidaMensal> findByItemAndAnoMes(Item item, String anoMes);
    long countByItemTipoAndAnoMesAndCurtidoTrue(com.bitt.tracker.domain.enums.TipoItem tipo, String anoMes);
}
