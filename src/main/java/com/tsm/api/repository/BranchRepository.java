package com.tsm.api.repository;
import com.tsm.api.entity.Branch;
import com.tsm.api.entity.BranchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID>{
    List<Branch> findByCommerceId(UUID commerceId);
    List<Branch> findByCommerceIdAndStatus(UUID commerceId, BranchStatus status);
}
