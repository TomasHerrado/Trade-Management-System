package com.tsm.api.service;
import com.tsm.api.dto.request.StockAdjustmentRequest;
import com.tsm.api.dto.request.StockRequest;
import com.tsm.api.dto.response.StockMovementResponse;
import com.tsm.api.dto.response.StockResponse;
import java.util.List;
import java.util.UUID;
public interface StockService {
    StockResponse createOrUpdate(UUID branchId, StockRequest request);
    StockResponse getByBranchAndVariant(UUID branchId, UUID variantId);
    List<StockResponse> getByBranchId(UUID branchId);
    List<StockResponse> getLowStockByBranchId(UUID branchId);
    StockMovementResponse adjust(UUID branchId, UUID variantId, StockAdjustmentRequest request);
    List<StockMovementResponse> getMovements(UUID branchId, UUID variantId);
}
