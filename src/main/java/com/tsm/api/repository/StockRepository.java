package com.tsm.api.repository;

import com.tsm.api.entity.Stock;
import com.tsm.api.repository.projection.LowStockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    List<Stock> findByBranchId(UUID branchId);
    Optional<Stock> findByBranchIdAndProductVariantId(UUID branchId, UUID productVariantId);
    List<Stock> findByProductVariantId(UUID productVariantId);

    @Query("SELECT s FROM Stock s WHERE s.branch.id = :branchId AND s.quantity <= s.minQuantity")
    List<Stock> findLowStockByBranchId(UUID branchId);

    @Query("SELECT st.productVariant.product.name AS productName, st.productVariant.name AS variantName, " +
            "st.branch.name AS branchName, st.quantity AS quantity, st.minQuantity AS minQuantity " +
            "FROM Stock st WHERE st.branch.commerce.id = :commerceId AND st.quantity <= st.minQuantity " +
            "ORDER BY st.quantity ASC")
    List<LowStockProjection> findLowStockByCommerceId(@Param("commerceId") UUID commerceId);
}