package com.tsm.api.repository;
import com.tsm.api.entity.Product;
import com.tsm.api.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>{
    List<Product> findByCommerceId(UUID commerceId);
    List<Product> findByCommerceIdAndStatus(UUID commerceId, ProductStatus status);
    List<Product> findByCategoryId(UUID categoryId);
}
