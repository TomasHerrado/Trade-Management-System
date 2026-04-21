package com.tsm.api.repository;
import com.tsm.api.entity.UserCommerce;
import com.tsm.api.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCommerceRepository extends JpaRepository<UserCommerce, UUID>{
    List<UserCommerce> findByUserId(UUID userId);
    List<UserCommerce> findByCommerceId(UUID commerceId);
    Optional<UserCommerce> findByUserIdAndCommerceId(UUID userId, UUID commerceId);
    boolean existsByUserIdAndCommerceId(UUID userId, UUID commerceId);
    List<UserCommerce> findByUserIdAndRole(UUID userId, UserRole role);
}
