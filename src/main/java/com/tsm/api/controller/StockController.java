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

    // Solo OWNER y ADMIN pueden crear o actualizar stock manualmente
    @PostMapping
    public ResponseEntity<StockResponse> createOrUpdate(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StockRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdminForBranch(userId, branchId);
        return ResponseEntity.ok(stockService.createOrUpdate(branchId, request));
    }

    // OWNER, ADMIN y EMPLOYEE pueden consultar el stock de su sucursal
    @GetMapping
    public ResponseEntity<List<StockResponse>> getByBranch(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getByBranchId(branchId));
    }

    // OWNER, ADMIN y EMPLOYEE pueden consultar alertas de stock bajo
    @GetMapping("/low")
    public ResponseEntity<List<StockResponse>> getLowStock(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getLowStockByBranchId(branchId));
    }

    // OWNER, ADMIN y EMPLOYEE pueden consultar el stock de una variante específica
    @GetMapping("/{variantId}")
    public ResponseEntity<StockResponse> getByVariant(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(stockService.getByBranchAndVariant(branchId, variantId));
    }

    // Solo OWNER y ADMIN pueden realizar ajustes de stock
    @PostMapping("/{variantId}/adjust")
    public ResponseEntity<StockMovementResponse> adjust(
            @PathVariable UUID branchId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StockAdjustmentRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdminForBranch(userId, branchId);
        return ResponseEntity.ok(stockService.adjust(branchId, variantId, request));
    }

    // OWNER, ADMIN y EMPLOYEE pueden ver los movimientos de stock (solo consulta)
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