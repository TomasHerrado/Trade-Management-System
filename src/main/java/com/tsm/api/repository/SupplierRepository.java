package com.tsm.api.repository;
import com.tsm.api.entity.Supplier;
import com.tsm.api.entity.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID>{
    List<Supplier> findByCommerceId(UUID commerceId);
    List<Supplier> findByCommerceIdAndStatus(UUID commerceId, SupplierStatus status);

    // proveedores con deuda pendiente
    @Query("SELECT s FROM Supplier s WHERE s.commerce.id = :commerceId AND s.debt > 0")
    List<Supplier> findWithDebtByCommerceId(UUID commerceId);
}
