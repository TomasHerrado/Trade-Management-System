package com.tsm.api.repository;
import com.tsm.api.entity.CashRegister;
import com.tsm.api.entity.CashRegisterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, UUID> {
    List<CashRegister> findByBranchId(UUID branchId);

    // solo puede haber una caja abierta por sucursal
    Optional<CashRegister> findByBranchIdAndStatus(UUID branchId, CashRegisterStatus status);
    boolean existsByBranchIdAndStatus(UUID branchId, CashRegisterStatus status);
}
