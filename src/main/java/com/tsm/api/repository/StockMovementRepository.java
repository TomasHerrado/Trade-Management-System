package com.tsm.api.repository;
import com.tsm.api.entity.StockMovement;
import com.tsm.api.entity.StockMovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID>{
    List<StockMovement> findByStockId(UUID stockId);
    List<StockMovement> findByStockIdAndType(UUID stockId, StockMovementType type);
}
