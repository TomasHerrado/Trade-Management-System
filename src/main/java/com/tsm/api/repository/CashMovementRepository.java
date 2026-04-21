package com.tsm.api.repository;
import com.tsm.api.entity.CashMovement;
import com.tsm.api.entity.CashMovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CashMovementRepository extends JpaRepository<CashMovement, UUID>{
    List<CashMovement> findByCashRegisterId(UUID cashRegisterId);
    List<CashMovement> findByCashRegisterIdAndType(UUID cashRegisterId, CashMovementType type);

    // total de movimientos de una caja
    @Query("SELECT COALESCE(SUM(cm.amount), 0) FROM CashMovement cm WHERE cm.cashRegister.id = :cashRegisterId")
    BigDecimal sumByCashRegisterId(UUID cashRegisterId);
}
