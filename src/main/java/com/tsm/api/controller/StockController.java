package com.tsm.api.controller;
import com.tsm.api.dto.request.StockAdjustmentRequest;
import com.tsm.api.dto.request.StockRequest;
import com.tsm.api.dto.response.StockMovementResponse;
import com.tsm.api.dto.response.StockResponse;
import com.tsm.api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches/{branchId}/stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> createOrUpdate(
            @PathVariable UUID branchId,
            @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(stockService.createOrUpdate(branchId, request));
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(stockService.getByBranchId(branchId));
    }

    @GetMapping("/low")
    public ResponseEntity<List<StockResponse>> getLowStock(@PathVariable UUID branchId) {
        return ResponseEntity.ok(stockService.getLowStockByBranchId(branchId));
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<StockResponse> getByVariant(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId) {
        return ResponseEntity.ok(stockService.getByBranchAndVariant(branchId, variantId));
    }

    @PostMapping("/{variantId}/adjust")
    public ResponseEntity<StockMovementResponse> adjust(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(stockService.adjust(branchId, variantId, request));
    }

    @GetMapping("/{variantId}/movements")
    public ResponseEntity<List<StockMovementResponse>> getMovements(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId) {
        return ResponseEntity.ok(stockService.getMovements(branchId, variantId));
    }
}
