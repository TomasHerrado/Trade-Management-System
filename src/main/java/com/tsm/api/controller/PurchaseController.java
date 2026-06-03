package com.tsm.api.controller;

import com.tsm.api.dto.request.PurchaseRequest;
import com.tsm.api.dto.response.PurchaseResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches/{branchId}/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PurchaseRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.status(201).body(purchaseService.create(branchId, userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getByBranch(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(purchaseService.getByBranchId(branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(
            @PathVariable UUID branchId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseResponse>> getBySupplier(
            @PathVariable UUID branchId,
            @PathVariable UUID supplierId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateBranchAccess(userId, branchId);
        return ResponseEntity.ok(purchaseService.getBySupplierId(supplierId));
    }

}
