package com.tsm.api.service;
import com.tsm.api.dto.request.PurchaseRequest;
import com.tsm.api.dto.response.PurchaseResponse;
import java.util.List;
import java.util.UUID;

public interface PurchaseService {
    PurchaseResponse create(UUID branchId, UUID userId, PurchaseRequest request);
    PurchaseResponse getById(UUID id);
    List<PurchaseResponse> getByBranchId(UUID branchId);
    List<PurchaseResponse> getBySupplierId(UUID supplierId);
}
