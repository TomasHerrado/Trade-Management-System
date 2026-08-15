package com.tsm.api.service.impl;

import com.tsm.api.dto.request.BulkPriceUpdateRequest;
import com.tsm.api.dto.request.PriceUpdateTarget;
import com.tsm.api.dto.request.ProductVariantRequest;
import com.tsm.api.dto.response.BulkPriceUpdateResponse;
import com.tsm.api.dto.response.ProductVariantResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.*;
import com.tsm.api.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductServiceImpl productService;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SaleItemRepository saleItemRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;

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

    @Override
    @Transactional
    public ProductVariantResponse activate(UUID id) {
        ProductVariant variant = findById(id);
        variant.setStatus(ProductStatus.ACTIVE);
        return toResponse(productVariantRepository.save(variant));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ProductVariant variant = findById(id);

        boolean hasSales = saleItemRepository.existsByProductVariantId(id);
        boolean hasPurchases = purchaseItemRepository.existsByProductVariantId(id);
        if (hasSales || hasPurchases) {
            throw new BusinessException(
                    "No se puede eliminar la variante porque tiene ventas o compras registradas. " +
                            "Podés desactivarla en su lugar."
            );
        }

        List<Stock> stocks = stockRepository.findByProductVariantId(id);
        for (Stock stock : stocks) {
            stockMovementRepository.deleteAll(stockMovementRepository.findByStockId(stock.getId()));
        }
        stockRepository.deleteAll(stocks);

        productVariantRepository.delete(variant);
    }

    public ProductVariant findById(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variante no encontrada"));
    }

    @Override
    @Transactional
    public BulkPriceUpdateResponse bulkUpdateBySupplier(UUID commerceId, BulkPriceUpdateRequest request) {
        List<Product> products = productRepository.findByCommerceIdAndSupplierId(commerceId, request.getSupplierId());
        if (products.isEmpty()) {
            throw new BusinessException("Ese proveedor no tiene productos cargados");
        }

        List<UUID> productIds = products.stream().map(Product::getId).toList();
        List<ProductVariant> variants = productVariantRepository.findByProductIdIn(productIds);

        // factor: 5% -> 1.05 | -10% -> 0.90
        BigDecimal factor = BigDecimal.ONE.add(
                request.getPercentage().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
        );

        for (ProductVariant variant : variants) {
            if (request.getApplyTo() == PriceUpdateTarget.PRICE || request.getApplyTo() == PriceUpdateTarget.BOTH) {
                variant.setPrice(variant.getPrice().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            }
            if (request.getApplyTo() == PriceUpdateTarget.COST || request.getApplyTo() == PriceUpdateTarget.BOTH) {
                variant.setCost(variant.getCost().multiply(factor).setScale(2, RoundingMode.HALF_UP));
            }
        }
        productVariantRepository.saveAll(variants);

        return BulkPriceUpdateResponse.builder().updatedCount(variants.size()).build();
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