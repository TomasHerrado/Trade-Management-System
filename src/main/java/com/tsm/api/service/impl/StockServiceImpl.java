package com.tsm.api.service.impl;

import com.tsm.api.dto.request.StockAdjustmentRequest;
import com.tsm.api.dto.request.StockRequest;
import com.tsm.api.dto.response.StockMovementResponse;
import com.tsm.api.dto.response.StockResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.StockMovementRepository;
import com.tsm.api.repository.StockRepository;
import com.tsm.api.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BranchServiceImpl branchService;
    private final ProductVariantServiceImpl productVariantService;

    @Override
    @Transactional
    public StockResponse createOrUpdate(UUID branchId, StockRequest request) {
        Branch branch = branchService.findById(branchId);
        ProductVariant variant = productVariantService.findById(request.getProductVariantId());

        Stock stock = stockRepository
                .findByBranchIdAndProductVariantId(branchId, variant.getId())
                .orElse(Stock.builder()
                        .branch(branch)
                        .productVariant(variant)
                        .quantity(0)
                        .minQuantity(0)
                        .build());

        stock.setQuantity(request.getQuantity());
        if (request.getMinQuantity() != null) {
            stock.setMinQuantity(request.getMinQuantity());
        }
        return toResponse(stockRepository.save(stock));
    }

    @Override
    public StockResponse getByBranchAndVariant(UUID branchId, UUID variantId) {
        Stock stock = stockRepository.findByBranchIdAndProductVariantId(branchId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
        return toResponse(stock);
    }

    @Override
    public List<StockResponse> getByBranchId(UUID branchId) {
        return stockRepository.findByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StockResponse> getLowStockByBranchId(UUID branchId) {
        return stockRepository.findLowStockByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public StockMovementResponse adjust(UUID branchId, UUID variantId, StockAdjustmentRequest request) {
        Stock stock = stockRepository.findByBranchIdAndProductVariantId(branchId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));

        int newQuantity = stock.getQuantity() + request.getQuantity();
        if (newQuantity < 0) {
            throw new com.tsm.api.exception.BusinessException("Stock insuficiente");
        }
        stock.setQuantity(newQuantity);
        stockRepository.save(stock);

        StockMovement movement = StockMovement.builder()
                .stock(stock)
                .type(request.getType())
                .quantity(request.getQuantity())
                .quantityAfter(newQuantity)
                .note(request.getNote())
                .build();
        return toMovementResponse(stockMovementRepository.save(movement));
    }

    @Override
    public List<StockMovementResponse> getMovements(UUID branchId, UUID variantId) {
        Stock stock = stockRepository.findByBranchIdAndProductVariantId(branchId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
        return stockMovementRepository.findByStockId(stock.getId()).stream()
                .map(this::toMovementResponse)
                .toList();
    }

    public Stock findByBranchAndVariant(UUID branchId, UUID variantId) {
        return stockRepository.findByBranchIdAndProductVariantId(branchId, variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado"));
    }

    public StockResponse toResponse(Stock stock) {
        ProductVariant variant = stock.getProductVariant();
        return StockResponse.builder()
                .id(stock.getId())
                .branchId(stock.getBranch().getId())
                .branchName(stock.getBranch().getName())
                .productVariantId(variant.getId())
                .productName(variant.getProduct().getName())
                .variantName(variant.getName())
                .sku(variant.getSku())
                .quantity(stock.getQuantity())
                .minQuantity(stock.getMinQuantity())
                .lowStock(stock.getQuantity() <= stock.getMinQuantity())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    private StockMovementResponse toMovementResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .stockId(movement.getStock().getId())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .quantityAfter(movement.getQuantityAfter())
                .note(movement.getNote())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}