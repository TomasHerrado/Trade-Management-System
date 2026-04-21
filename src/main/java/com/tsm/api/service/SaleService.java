package com.tsm.api.service;
import com.tsm.api.dto.request.SaleRequest;
import com.tsm.api.dto.response.SaleResponse;
import java.util.List;
import java.util.UUID;
public interface SaleService {
    SaleResponse create(UUID branchId, UUID userId, SaleRequest request);
    SaleResponse getById(UUID id);
    List<SaleResponse> getByBranchId(UUID branchId);
    List<SaleResponse> getByCustomerId(UUID customerId);
}
