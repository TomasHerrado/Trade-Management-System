package com.tsm.api.controller;

import com.tsm.api.dto.request.BranchRequest;
import com.tsm.api.dto.response.BranchResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<BranchResponse> create(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BranchRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(branchService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getByCommerce(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(branchService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getById(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(branchService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> update(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody BranchRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(branchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        branchService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

}
