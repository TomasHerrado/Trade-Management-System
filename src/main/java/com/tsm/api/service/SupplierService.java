package com.tsm.api.service;
import com.tsm.api.dto.request.PaymentRequest;
import com.tsm.api.dto.request.SupplierRequest;
import com.tsm.api.dto.response.SupplierResponse;
import java.util.List;
import java.util.UUID;
public interface SupplierService {
    SupplierResponse create(UUID commerceId, SupplierRequest request);
    SupplierResponse getById(UUID id);
    List<SupplierResponse> getByCommerceId(UUID commerceId);
    SupplierResponse update(UUID id, SupplierRequest request);
    void deactivate(UUID id);
    SupplierResponse registerPayment(UUID supplierId, UUID branchId, PaymentRequest request);
    List<SupplierResponse> getWithDebt(UUID commerceId);
}
