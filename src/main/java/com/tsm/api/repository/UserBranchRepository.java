package com.tsm.api.repository;

import com.tsm.api.entity.UserBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, UUID> {

    List<UserBranch> findByUserId(UUID userId);

    List<UserBranch> findByBranchId(UUID branchId);

    Optional<UserBranch> findByUserIdAndBranchId(UUID userId, UUID branchId);

    boolean existsByUserIdAndBranchId(UUID userId, UUID branchId);

    @Modifying
    @Query("DELETE FROM UserBranch ub WHERE ub.user.id = :userId AND ub.branch.id = :branchId")
    void deleteByUserIdAndBranchId(@Param("userId") UUID userId,
                                   @Param("branchId") UUID branchId);
}