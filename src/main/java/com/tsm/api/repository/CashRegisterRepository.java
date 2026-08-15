package com.tsm.api.repository;

import com.tsm.api.entity.CashRegister;
import com.tsm.api.entity.CashRegisterStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashRegisterRepository extends JpaRepository<CashRegister, UUID> {
    List<CashRegister> findByBranchId(UUID branchId);

    Optional<CashRegister> findByBranchIdAndStatus(UUID branchId, CashRegisterStatus status);
    boolean existsByBranchIdAndStatus(UUID branchId, CashRegisterStatus status);

    @Query("SELECT c FROM CashRegister c WHERE c.branch.id = :branchId " +
            "AND c.openedAt BETWEEN :from AND :to " +
            "ORDER BY c.openedAt DESC")
    List<CashRegister> findHistory(@Param("branchId") UUID branchId,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to,
                                   Pageable pageable);
}