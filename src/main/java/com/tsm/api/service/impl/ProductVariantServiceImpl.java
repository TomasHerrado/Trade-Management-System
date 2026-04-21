package com.tsm.api.service.impl;

import com.tsm.api.dto.request.ProductVariantRequest;
import com.tsm.api.dto.response.ProductVariantResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.ProductVariantRepository;
import com.tsm.api.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductServiceImpl productService;

    @Override
    @Transactional
    public ProductVariantResponse create(UUID productId, ProductVariantRequest request) {
        Product product = productService.findById(productId);
        if (request.getSku() != null && productVariantRepository.existsBySku(request.getSku())) {
            throw new BusinessException("El SKU ya está en uso");
        }
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .name(request.getName())
                .sku(request.getSku())
                .price(request.getPrice())
                .cost(request.getCost())
                .status(ProductStatus.ACTIVE)
                .build();
        return toResponse(productVariantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<ProductVariantResponse> getByProductId(UUID productId) {
        return productVariantRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductVariantResponse update(UUID id, ProductVariantRequest request) {
        ProductVariant variant = findById(id);
        if (request.getSku() != null && !request.getSku().equals(variant.getSku())
                && productVariantRepository.existsBySku(request.getSku())) {
            throw new BusinessException("El SKU ya está en uso");
        }
        variant.setName(request.getName());
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setCost(request.getCost());
        return toResponse(productVariantRepository.save(variant));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        ProductVariant variant = findById(id);
        variant.setStatus(ProductStatus.INACTIVE);
        productVariantRepository.save(variant);
    }

    public ProductVariant findById(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
    }

    public ProductVariantResponse toResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .name(variant.getName())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .cost(variant.getCost())
                .status(variant.getStatus())
                .createdAt(variant.getCreatedAt())
                .build();
    }
}