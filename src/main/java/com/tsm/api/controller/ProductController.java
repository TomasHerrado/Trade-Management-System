package com.tsm.api.controller;
import com.tsm.api.dto.request.ProductRequest;
import com.tsm.api.dto.request.ProductVariantRequest;
import com.tsm.api.dto.response.ProductResponse;
import com.tsm.api.dto.response.ProductVariantResponse;
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

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @PathVariable UUID commerceId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(201).body(productService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getByCommerce(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(productService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ── Variantes ──────────────────────────────────────────

    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.status(201).body(productVariantService.create(productId, request));
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariants(@PathVariable UUID productId) {
        return ResponseEntity.ok(productVariantService.getByProductId(productId));
    }

    @GetMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(@PathVariable UUID variantId) {
        return ResponseEntity.ok(productVariantService.getById(variantId));
    }

    @PutMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable UUID variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(productVariantService.update(variantId, request));
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<Void> deactivateVariant(@PathVariable UUID variantId) {
        productVariantService.deactivate(variantId);
        return ResponseEntity.noContent().build();
    }
}
