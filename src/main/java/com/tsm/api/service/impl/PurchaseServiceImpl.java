package com.tsm.api.service.impl;

import com.tsm.api.dto.request.PurchaseRequest;
import com.tsm.api.dto.response.PurchaseItemResponse;
import com.tsm.api.dto.response.PurchaseResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.PurchaseRepository;
import com.tsm.api.repository.StockMovementRepository;
import com.tsm.api.repository.StockRepository;
import com.tsm.api.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BranchServiceImpl branchService;
    private final UserServiceImpl userService;
    private final SupplierServiceImpl supplierService;
    private final ProductVariantServiceImpl productVariantService;
    private final CashRegisterServiceImpl cashRegisterService; // NUEVO: para registrar movimiento de caja

    @Override
    @Transactional
    public PurchaseResponse create(UUID branchId, UUID userId, PurchaseRequest request) {
        Branch branch = branchService.findById(branchId);
        User user = userService.findById(userId);
        Supplier supplier = supplierService.findById(request.getSupplierId());

        List<PurchaseItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            ProductVariant variant = productVariantService.findById(itemRequest.getProductVariantId());
            BigDecimal subtotal = itemRequest.getUnitCost()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            PurchaseItem item = PurchaseItem.builder()
                    .productVariant(variant)
                    .quantity(itemRequest.getQuantity())
                    .unitCost(itemRequest.getUnitCost())
                    .subtotal(subtotal)
                    .build();
            items.add(item);
            total = total.add(subtotal);

            // sumar al stock
            Stock stock = stockRepository
                    .findByBranchIdAndProductVariantId(branchId, variant.getId())
                    .orElse(Stock.builder()
                            .branch(branch)
                            .productVariant(variant)
                            .quantity(0)
                            .minQuantity(0)
                            .build());

            int newQuantity = stock.getQuantity() + itemRequest.getQuantity();
            stock.setQuantity(newQuantity);
            stockRepository.save(stock);

            StockMovement movement = StockMovement.builder()
                    .stock(stock)
                    .type(StockMovementType.PURCHASE)
                    .quantity(itemRequest.getQuantity())
                    .quantityAfter(newQuantity)
                    .note("Compra registrada")
                    .build();
            stockMovementRepository.save(movement);
        }

        Purchase purchase = Purchase.builder()
                .branch(branch)
                .supplier(supplier)
                .user(user)
                .total(total)
                .paymentType(request.getPaymentType())
                .status(PurchaseStatus.COMPLETED)
                .note(request.getNote())
                .items(items)
                .build();
        items.forEach(item -> item.setPurchase(purchase));
        purchaseRepository.save(purchase);

        // FIX: solo suma deuda si la compra es a cuenta corriente (ACCOUNT).
        // Antes se sumaba siempre, sin importar el paymentType.
        if (request.getPaymentType() == PaymentType.ACCOUNT) {
            supplierService.addDebt(supplier.getId(), total);
        } else {
            // FIX: para compras que sí se pagan al momento (CASH/CARD/TRANSFER/MIXED),
            // registramos la salida de dinero en la caja de la sucursal.
            CashRegister cashRegister = cashRegisterService.findOpenByBranchId(branchId);
            cashRegisterService.registerMovement(
                    cashRegister,
                    CashMovementType.SUPPLIER_PAYMENT,
                    total.negate(),
                    "Compra a proveedor - " + supplier.getName(),
                    purchase.getId());
        }

        return toResponse(purchase);
    }

    @Override
    public PurchaseResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<PurchaseResponse> getByBranchId(UUID branchId) {
        return purchaseRepository.findByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<PurchaseResponse> getBySupplierId(UUID supplierId) {
        return purchaseRepository.findBySupplierId(supplierId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Purchase findById(UUID id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada"));
    }

    private PurchaseResponse toResponse(Purchase purchase) {
        List<PurchaseItemResponse> itemResponses = purchase.getItems().stream()
                .map(item -> PurchaseItemResponse.builder()
                        .id(item.getId())
                        .productVariantId(item.getProductVariant().getId())
                        .productName(item.getProductVariant().getProduct().getName())
                        .variantName(item.getProductVariant().getName())
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitCost())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .branchId(purchase.getBranch().getId())
                .branchName(purchase.getBranch().getName())
                .supplierId(purchase.getSupplier().getId())
                .supplierName(purchase.getSupplier().getName())
                .userName(purchase.getUser().getFirstName() + " " + purchase.getUser().getLastName())
                .total(purchase.getTotal())
                .paymentType(purchase.getPaymentType())
                .status(purchase.getStatus())
                .note(purchase.getNote())
                .items(itemResponses)
                .createdAt(purchase.getCreatedAt())
                .build();
    }
}