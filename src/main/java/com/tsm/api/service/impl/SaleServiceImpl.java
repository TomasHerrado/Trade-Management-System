package com.tsm.api.service.impl;

import com.tsm.api.dto.request.SaleRequest;
import com.tsm.api.dto.response.SaleItemResponse;
import com.tsm.api.dto.response.SaleResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.CustomerAccountRepository;
import com.tsm.api.repository.SaleRepository;
import com.tsm.api.repository.StockMovementRepository;
import com.tsm.api.repository.StockRepository;
import com.tsm.api.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository; // FIX: faltaba esta dependencia
    private final CustomerAccountRepository customerAccountRepository;
    private final BranchServiceImpl branchService;
    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final ProductVariantServiceImpl productVariantService;
    private final CashRegisterServiceImpl cashRegisterService;

    @Override
    @Transactional
    public SaleResponse create(UUID branchId, UUID userId, SaleRequest request) {
        Branch branch = branchService.findById(branchId);
        User user = userService.findById(userId);

        CashRegister cashRegister = cashRegisterService.findOpenByBranchId(branchId);

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerService.findById(request.getCustomerId());
        }

        if (request.getPaymentType() == PaymentType.ACCOUNT && customer == null) {
            throw new BusinessException("Se requiere un cliente para ventas en cuenta corriente");
        }

        List<SaleItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.getItems()) {
            ProductVariant variant = productVariantService.findById(itemRequest.getProductVariantId());

            // FIX: validar que la variante pertenece al mismo comercio que la sucursal
            if (!variant.getProduct().getCommerce().getId()
                    .equals(branch.getCommerce().getId())) {
                throw new BusinessException(
                        "El producto '" + variant.getProduct().getName()
                                + "' no pertenece a este comercio");
            }

            Stock stock = stockRepository
                    .findByBranchIdAndProductVariantId(branchId, variant.getId())
                    .orElseThrow(() -> new BusinessException(
                            "No hay stock registrado para: " + variant.getName()));

            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new BusinessException("Stock insuficiente para: " + variant.getName()
                        + ". Disponible: " + stock.getQuantity());
            }

            BigDecimal subtotal = variant.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            SaleItem item = SaleItem.builder()
                    .productVariant(variant)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(variant.getPrice())
                    .subtotal(subtotal)
                    .build();
            items.add(item);
            total = total.add(subtotal);

            int newQuantity = stock.getQuantity() - itemRequest.getQuantity();
            stock.setQuantity(newQuantity);
            stockRepository.save(stock);

            // FIX: se construía el movimiento pero nunca se guardaba
            StockMovement movement = StockMovement.builder()
                    .stock(stock)
                    .type(StockMovementType.SALE)
                    .quantity(-itemRequest.getQuantity())
                    .quantityAfter(newQuantity)
                    .note("Venta registrada")
                    .build();
            stockMovementRepository.save(movement);
        }

        Sale sale = Sale.builder()
                .branch(branch)
                .cashRegister(cashRegister)
                .customer(customer)
                .user(user)
                .total(total)
                .paymentType(request.getPaymentType())
                .status(SaleStatus.COMPLETED)
                .note(request.getNote())
                .items(items)
                .build();
        items.forEach(item -> item.setSale(sale));
        saleRepository.save(sale);

        cashRegisterService.registerMovement(
                cashRegister,
                CashMovementType.SALE,
                total,
                "Venta",
                sale.getId());

        if (request.getPaymentType() == PaymentType.ACCOUNT) {
            CustomerAccount account = customerAccountRepository
                    .findByCustomerId(customer.getId())
                    .orElse(CustomerAccount.builder()
                            .customer(customer)
                            .branch(branch)
                            .balance(BigDecimal.ZERO)
                            .build());
            account.setBalance(account.getBalance().add(total));
            customerAccountRepository.save(account);
        }

        return toResponse(sale);
    }

    @Override
    public SaleResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<SaleResponse> getByBranchId(UUID branchId) {
        return saleRepository.findByBranchId(branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<SaleResponse> getByCustomerId(UUID customerId) {
        return saleRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Sale findById(UUID id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(item -> SaleItemResponse.builder()
                        .id(item.getId())
                        .productVariantId(item.getProductVariant().getId())
                        .productName(item.getProductVariant().getProduct().getName())
                        .variantName(item.getProductVariant().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .branchId(sale.getBranch().getId())
                .branchName(sale.getBranch().getName())
                .cashRegisterId(sale.getCashRegister().getId())
                .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .customerName(sale.getCustomer() != null
                        ? sale.getCustomer().getFirstName() + " " + sale.getCustomer().getLastName()
                        : null)
                .userName(sale.getUser().getFirstName() + " " + sale.getUser().getLastName())
                .total(sale.getTotal())
                .paymentType(sale.getPaymentType())
                .status(sale.getStatus())
                .note(sale.getNote())
                .items(itemResponses)
                .createdAt(sale.getCreatedAt())
                .build();
    }

}