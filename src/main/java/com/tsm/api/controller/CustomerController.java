package com.tsm.api.controller;

import com.tsm.api.dto.request.CustomerRequest;
import com.tsm.api.dto.request.PaymentRequest;
import com.tsm.api.dto.response.CustomerAccountResponse;
import com.tsm.api.dto.response.CustomerResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CustomerRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(customerService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getByCommerce(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CustomerRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        customerService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ── Cuenta corriente ────────────────────────────────────

    @GetMapping("/{id}/account")
    public ResponseEntity<CustomerAccountResponse> getAccount(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.getAccount(id));
    }

    @PostMapping("/{id}/account/payment")
    public ResponseEntity<CustomerAccountResponse> registerPayment(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestParam UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PaymentRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.registerPayment(id, branchId, request));
    }

    @GetMapping("/debtors")
    public ResponseEntity<List<CustomerAccountResponse>> getDebtors(
            @PathVariable UUID commerceId,
            @RequestParam UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(customerService.getDebtorsByBranch(branchId));
    }

}
