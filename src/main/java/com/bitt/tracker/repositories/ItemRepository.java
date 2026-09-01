package com.bitt.tracker.repositories;
import com.bitt.tracker.domain.entities.Item;
import com.bitt.tracker.domain.enums.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.curtidas c ON c.anoMes = :anoMes WHERE i.tipo = :tipo ORDER BY i.id")
    List<Item> findByTipoWithCurtidas(@Param("tipo") TipoItem tipo, @Param("anoMes") String anoMes);
    long countByTipo(TipoItem tipo);
}
