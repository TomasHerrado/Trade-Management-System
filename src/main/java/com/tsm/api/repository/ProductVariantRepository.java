package com.tsm.api.repository;
import com.tsm.api.entity.ProductVariant;
import com.tsm.api.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID>{
    List<ProductVariant> findByProductId(UUID productId);
    List<ProductVariant> findByProductIdAndStatus(UUID productId, ProductStatus status);
    Optional<ProductVariant> findBySku(String sku);
    boolean existsBySku(String sku);
}
