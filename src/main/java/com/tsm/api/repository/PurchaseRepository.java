package com.tsm.api.repository;
import com.tsm.api.entity.Purchase;
import com.tsm.api.entity.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID>{
    List<Purchase> findByBranchId(UUID branchId);
    List<Purchase> findBySupplierId(UUID supplierId);
    List<Purchase> findByBranchIdAndStatus(UUID branchId, PurchaseStatus status);
}
