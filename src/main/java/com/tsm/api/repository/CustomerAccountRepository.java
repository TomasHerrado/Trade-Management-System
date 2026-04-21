package com.tsm.api.repository;
import com.tsm.api.entity.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, UUID>{
    Optional<CustomerAccount> findByCustomerId(UUID customerId);

    // clientes con deuda pendiente en una sucursal
    @Query("SELECT ca FROM CustomerAccount ca WHERE ca.branch.id = :branchId AND ca.balance > 0")
    List<CustomerAccount> findDebtorsByBranchId(UUID branchId);
}
