package com.tsm.api.controller;

import com.tsm.api.dto.request.ProductRequest;
import com.tsm.api.dto.request.ProductVariantRequest;
import com.tsm.api.dto.response.ProductResponse;
import com.tsm.api.dto.response.ProductVariantResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.ProductService;
import com.tsm.api.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/products")
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;
    private final ProductVariantService productVariantService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(productService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getByCommerce(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ── Variantes ──────────────────────────────────────────

    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductVariantRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(productVariantService.create(productId, request));
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariants(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productVariantService.getByProductId(productId));
    }

    @GetMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productVariantService.getById(variantId));
    }

    @PutMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductVariantRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productVariantService.update(variantId, request));
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Void> deactivateVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        productVariantService.deactivate(variantId);
        return ResponseEntity.noContent().build();
    }

}
