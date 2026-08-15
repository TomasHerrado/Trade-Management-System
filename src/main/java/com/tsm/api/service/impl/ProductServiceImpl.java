package com.tsm.api.service.impl;

import com.tsm.api.dto.request.ProductRequest;
import com.tsm.api.dto.response.ProductResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.*;
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
    private final ProductVariantRepository productVariantRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final SaleItemRepository saleItemRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final CommerceServiceImpl commerceService;
    private final CategoryServiceImpl categoryService;
    private final SupplierServiceImpl supplierService;

    @Override
    @Transactional
    public ProductResponse create(UUID commerceId, ProductRequest request) {
        if (productRepository.existsByNameAndCommerceId(request.getName(), commerceId)) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }
        Commerce commerce = commerceService.findById(commerceId);
        Category category = request.getCategoryId() != null ? categoryService.findById(request.getCategoryId()) : null;
        Supplier supplier = resolveSupplier(request.getSupplierId(), commerceId); // nuevo

        Product product = Product.builder()
                .commerce(commerce)
                .category(category)
                .supplier(supplier)          // nuevo
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
    public List<ProductResponse> getByCommerceIdAndSupplier(UUID commerceId, UUID supplierId) {
        return productRepository.findByCommerceIdAndSupplierId(commerceId, supplierId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findById(id);
        UUID commerceId = product.getCommerce().getId();

        if (productRepository.existsByNameAndCommerceIdAndIdNot(request.getName(), commerceId, id)) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }

        Category category = request.getCategoryId() != null ? categoryService.findById(request.getCategoryId()) : null;
        Supplier supplier = resolveSupplier(request.getSupplierId(), commerceId); // nuevo

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setSupplier(supplier); // nuevo
        return toResponse(productRepository.save(product));
    }

    private Supplier resolveSupplier(UUID supplierId, UUID commerceId) {
        if (supplierId == null) return null;
        Supplier supplier = supplierService.findById(supplierId);
        if (!supplier.getCommerce().getId().equals(commerceId)) {
            throw new BusinessException("El proveedor no pertenece a este comercio");
        }
        return supplier;
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Product product = findById(id);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public ProductResponse activate(UUID id) {
        Product product = findById(id);
        product.setStatus(ProductStatus.ACTIVE);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = findById(id);
        List<ProductVariant> variants = productVariantRepository.findByProductId(id);

        for (ProductVariant variant : variants) {
            boolean hasSales = saleItemRepository.existsByProductVariantId(variant.getId());
            boolean hasPurchases = purchaseItemRepository.existsByProductVariantId(variant.getId());
            if (hasSales || hasPurchases) {
                throw new BusinessException(
                        "No se puede eliminar el producto porque tiene ventas o compras registradas. " +
                                "Podés desactivarlo en su lugar."
                );
            }
        }

        for (ProductVariant variant : variants) {
            List<Stock> stocks = stockRepository.findByProductVariantId(variant.getId());
            for (Stock stock : stocks) {
                stockMovementRepository.deleteAll(stockMovementRepository.findByStockId(stock.getId()));
            }
            stockRepository.deleteAll(stocks);
        }
        productVariantRepository.deleteAll(variants);
        productRepository.delete(product);
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
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }

}