package com.tsm.api.service.impl;

import com.tsm.api.dto.request.PaymentRequest;
import com.tsm.api.dto.request.SupplierRequest;
import com.tsm.api.dto.response.SupplierResponse;
import com.tsm.api.entity.*;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.exception.ResourceNotFoundException;
import com.tsm.api.repository.SupplierRepository;
import com.tsm.api.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    final SupplierRepository supplierRepository;
    private final CommerceServiceImpl commerceService;
    private final CashRegisterServiceImpl cashRegisterService; // FIX: agregado para registrar movimiento de caja

    @Override
    @Transactional
    public SupplierResponse create(UUID commerceId, SupplierRequest request) {
        Commerce commerce = commerceService.findById(commerceId);
        Supplier supplier = Supplier.builder()
                .commerce(commerce)
                .name(request.getName())
                .contactName(request.getContactName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .debt(java.math.BigDecimal.ZERO)
                .status(SupplierStatus.ACTIVE)
                .build();
        return toResponse(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<SupplierResponse> getByCommerceId(UUID commerceId) {
        return supplierRepository.findByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {
        Supplier supplier = findById(id);
        supplier.setName(request.getName());
        supplier.setContactName(request.getContactName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        return toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Supplier supplier = findById(id);
        supplier.setStatus(SupplierStatus.INACTIVE);
        supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse registerPayment(UUID supplierId, UUID branchId, PaymentRequest request) {
        Supplier supplier = findById(supplierId);

        if (supplier.getDebt().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("El pago supera la deuda con el proveedor");
        }

        supplier.setDebt(supplier.getDebt().subtract(request.getAmount()));
        supplierRepository.save(supplier);

        // FIX: registrar egreso en caja (nosotros le pagamos al proveedor)
        // el monto es negativo porque es una salida de dinero
        CashRegister cashRegister = cashRegisterService.findOpenByBranchId(branchId);
        cashRegisterService.registerMovement(
                cashRegister,
                CashMovementType.SUPPLIER_PAYMENT,
                request.getAmount().negate(),
                request.getDescription() != null ? request.getDescription() : "Pago a proveedor",
                supplierId);

        return toResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getWithDebt(UUID commerceId) {
        return supplierRepository.findWithDebtByCommerceId(commerceId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Supplier findById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .commerceId(supplier.getCommerce().getId())
                .name(supplier.getName())
                .contactName(supplier.getContactName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .debt(supplier.getDebt())
                .status(supplier.getStatus())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}