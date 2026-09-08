package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.CurtidaMensal;
import com.bitt.tracker.domain.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CurtidaMensalRepository extends JpaRepository<CurtidaMensal, Integer> {
    Optional<CurtidaMensal> findByItemAndAnoMes(Item item, String anoMes);

    @Query("SELECT c.item.tipo, COUNT(c.id) FROM CurtidaMensal c WHERE c.anoMes = :anoMes AND c.curtido = true GROUP BY c.item.tipo")
    List<Object[]> countCurtidasAgrupadasPorTipo(@Param("anoMes") String anoMes);
}
