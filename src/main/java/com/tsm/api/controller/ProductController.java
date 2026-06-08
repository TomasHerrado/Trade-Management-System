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

    // Solo OWNER y ADMIN pueden crear productos
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        return ResponseEntity.status(201).body(productService.create(commerceId, request));
    }

    // Cualquier miembro con acceso al comercio puede listar productos
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getByCommerce(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productService.getByCommerceId(commerceId));
    }

    // Cualquier miembro con acceso al comercio puede ver un producto
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productService.getById(id));
    }

    // Solo OWNER y ADMIN pueden editar productos
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        return ResponseEntity.ok(productService.update(id, request));
    }

    // Solo OWNER y ADMIN pueden desactivar productos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ── Variantes ──────────────────────────────────────────

    // Solo OWNER y ADMIN pueden crear variantes
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductVariantRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        return ResponseEntity.status(201).body(productVariantService.create(productId, request));
    }

    // Cualquier miembro con acceso al comercio puede listar variantes (para armar una venta)
    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariants(
            @PathVariable UUID commerceId,
            @PathVariable UUID productId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productVariantService.getByProductId(productId));
    }

    // Cualquier miembro con acceso al comercio puede ver una variante
    @GetMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(productVariantService.getById(variantId));
    }

    // Solo OWNER y ADMIN pueden editar variantes (incluye precios)
    @PutMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ProductVariantRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        return ResponseEntity.ok(productVariantService.update(variantId, request));
    }

    // Solo OWNER y ADMIN pueden desactivar variantes
    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Void> deactivateVariant(
            @PathVariable UUID commerceId,
            @PathVariable UUID variantId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        productVariantService.deactivate(variantId);
        return ResponseEntity.noContent().build();
    }
}