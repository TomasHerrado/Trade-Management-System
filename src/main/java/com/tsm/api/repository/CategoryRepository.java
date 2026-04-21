package com.tsm.api.repository;
import com.tsm.api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>{
    List<Category> findByCommerceId(UUID commerceId);
    boolean existsByNameAndCommerceId(String name, UUID commerceId);
}
