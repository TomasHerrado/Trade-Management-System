package com.tsm.api.repository;
import com.tsm.api.entity.Sale;
import com.tsm.api.entity.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID>{
    List<Sale> findByBranchId(UUID branchId);
    List<Sale> findByBranchIdAndStatus(UUID branchId, SaleStatus status);
    List<Sale> findByCustomerId(UUID customerId);
    List<Sale> findByCashRegisterId(UUID cashRegisterId);
}
