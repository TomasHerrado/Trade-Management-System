package com.tsm.api.controller;

import com.tsm.api.dto.request.CategoryRequest;
import com.tsm.api.dto.response.CategoryResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CategoryRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(categoryService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getByCommerce(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(categoryService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CategoryRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID commerceId,
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
