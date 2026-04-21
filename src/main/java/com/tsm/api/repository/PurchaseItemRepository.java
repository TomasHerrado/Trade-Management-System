package com.tsm.api.repository;
import com.tsm.api.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, UUID>{
    List<PurchaseItem> findByPurchaseId(UUID purchaseId);
    List<PurchaseItem> findByProductVariantId(UUID productVariantId);
}
