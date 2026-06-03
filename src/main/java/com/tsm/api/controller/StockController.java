package com.tsm.api.controller;

import com.tsm.api.dto.request.StockAdjustmentRequest;
import com.tsm.api.dto.request.StockRequest;
import com.tsm.api.dto.response.StockMovementResponse;
import com.tsm.api.dto.response.StockResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
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
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<StockResponse> createOrUpdate(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StockRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.createOrUpdate(branchId, request));
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getByBranch(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getByBranchId(branchId));
    }

    @GetMapping("/low")
    public ResponseEntity<List<StockResponse>> getLowStock(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getLowStockByBranchId(branchId));
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<StockResponse> getByVariant(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getByBranchAndVariant(branchId, variantId));
    }

    @PostMapping("/{variantId}/adjust")
    public ResponseEntity<StockMovementResponse> adjust(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StockAdjustmentRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.adjust(branchId, variantId, request));
    }

    @GetMapping("/{variantId}/movements")
    public ResponseEntity<List<StockMovementResponse>> getMovements(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getMovements(branchId, variantId));
    }

}
