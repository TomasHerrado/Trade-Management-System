package com.tsm.api.service.impl;

import com.tsm.api.dto.request.ProductRequest;
import com.tsm.api.dto.response.ProductResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.ProductRepository;
import com.tsm.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CommerceServiceImpl commerceService;
    private final CategoryServiceImpl categoryService;

    @Override
    @Transactional
    public ProductResponse create(UUID commerceId, ProductRequest request) {
        Commerce commerce = commerceService.findById(commerceId);
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryService.findById(request.getCategoryId());
        }
        Product product = Product.builder()
                .commerce(commerce)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(ProductStatus.ACTIVE)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<ProductResponse> getByCommerceId(UUID commerceId) {
        return productRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findById(id);
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryService.findById(request.getCategoryId());
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Product product = findById(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .commerceId(product.getCommerce().getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }
}