package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.domain.enums.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i, (CASE WHEN c.id IS NOT NULL THEN true ELSE false END) FROM Item i LEFT JOIN CurtidaMensal c ON c.item = i AND c.anoMes = :anoMes AND c.curtido = true WHERE i.tipo = :tipo ORDER BY i.id")
    List<Object[]> findItensComStatusCurtida(@Param("tipo") TipoItem tipo, @Param("anoMes") String anoMes);
    long countByTipo(TipoItem tipo);
}
