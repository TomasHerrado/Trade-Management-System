package com.tsm.api.repository;
import com.tsm.api.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    List<Stock> findByBranchId(UUID branchId);
    Optional<Stock> findByBranchIdAndProductVariantId(UUID branchId, UUID productVariantId);

    // alertas de stock bajo
    @Query("SELECT s FROM Stock s WHERE s.branch.id = :branchId AND s.quantity <= s.minQuantity")
    List<Stock> findLowStockByBranchId(UUID branchId);
}
