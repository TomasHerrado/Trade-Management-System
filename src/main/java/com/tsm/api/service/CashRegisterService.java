package com.tsm.api.service;
import com.tsm.api.dto.request.CashRegisterOpenRequest;
import com.tsm.api.dto.response.CashMovementResponse;
import com.tsm.api.dto.response.CashRegisterResponse;
import java.util.List;
import java.util.UUID;
public interface CashRegisterService {
    CashRegisterResponse open(UUID branchId, UUID userId, CashRegisterOpenRequest request);
    CashRegisterResponse close(UUID branchId, UUID userId);
    CashRegisterResponse getOpenByBranchId(UUID branchId);
    List<CashRegisterResponse> getHistoryByBranchId(UUID branchId);
    List<CashMovementResponse> getMovements(UUID cashRegisterId);
}
