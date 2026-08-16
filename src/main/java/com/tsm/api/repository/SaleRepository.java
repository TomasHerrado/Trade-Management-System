package com.tsm.api.repository;

import com.tsm.api.entity.Sale;
import com.tsm.api.entity.SaleStatus;
import com.tsm.api.repository.projection.BranchSalesProjection;
import com.tsm.api.repository.projection.PaymentTypeSalesProjection;
import com.tsm.api.repository.projection.TopProductProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID>{
    List<Sale> findByBranchId(UUID branchId);
    List<Sale> findByBranchIdAndStatus(UUID branchId, SaleStatus status);
    List<Sale> findByCustomerId(UUID customerId);
    List<Sale> findByCashRegisterId(UUID cashRegisterId);
    boolean existsByBranchId(UUID branchId);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sale s " +
            "WHERE s.branch.commerce.id = :commerceId AND s.status = :status " +
            "AND s.createdAt >= :from AND s.createdAt < :to")
    BigDecimal sumTotalByCommerceAndDateRange(@Param("commerceId") UUID commerceId,
                                              @Param("status") SaleStatus status,
                                              @Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);

    @Query("SELECT si.productVariant.product.name AS productName, si.productVariant.name AS variantName, " +
            "SUM(si.quantity) AS quantity, SUM(si.subtotal) AS revenue " +
            "FROM SaleItem si WHERE si.sale.branch.commerce.id = :commerceId " +
            "AND si.sale.status = :status AND si.sale.createdAt >= :from AND si.sale.createdAt < :to " +
            "GROUP BY si.productVariant.product.name, si.productVariant.name " +
            "ORDER BY SUM(si.quantity) DESC")
    List<TopProductProjection> findTopProductsByQuantity(@Param("commerceId") UUID commerceId,
                                                         @Param("status") SaleStatus status,
                                                         @Param("from") LocalDateTime from,
                                                         @Param("to") LocalDateTime to,
                                                         Pageable pageable);

    @Query("SELECT si.productVariant.product.name AS productName, si.productVariant.name AS variantName, " +
            "SUM(si.quantity) AS quantity, SUM(si.subtotal) AS revenue " +
            "FROM SaleItem si WHERE si.sale.branch.commerce.id = :commerceId " +
            "AND si.sale.status = :status AND si.sale.createdAt >= :from AND si.sale.createdAt < :to " +
            "GROUP BY si.productVariant.product.name, si.productVariant.name " +
            "ORDER BY SUM(si.subtotal) DESC")
    List<TopProductProjection> findTopProductsByRevenue(@Param("commerceId") UUID commerceId,
                                                        @Param("status") SaleStatus status,
                                                        @Param("from") LocalDateTime from,
                                                        @Param("to") LocalDateTime to,
                                                        Pageable pageable);

    @Query("SELECT s.branch.name AS branchName, SUM(s.total) AS total FROM Sale s " +
            "WHERE s.branch.commerce.id = :commerceId AND s.status = :status " +
            "AND s.createdAt >= :from AND s.createdAt < :to " +
            "GROUP BY s.branch.name ORDER BY SUM(s.total) DESC")
    List<BranchSalesProjection> findSalesByBranch(@Param("commerceId") UUID commerceId,
                                                  @Param("status") SaleStatus status,
                                                  @Param("from") LocalDateTime from,
                                                  @Param("to") LocalDateTime to);

    @Query("SELECT s.paymentType AS paymentType, SUM(s.total) AS total FROM Sale s " +
            "WHERE s.branch.commerce.id = :commerceId AND s.status = :status " +
            "AND s.createdAt >= :from AND s.createdAt < :to " +
            "GROUP BY s.paymentType ORDER BY SUM(s.total) DESC")
    List<PaymentTypeSalesProjection> findSalesByPaymentType(@Param("commerceId") UUID commerceId,
                                                            @Param("status") SaleStatus status,
                                                            @Param("from") LocalDateTime from,
                                                            @Param("to") LocalDateTime to);
}