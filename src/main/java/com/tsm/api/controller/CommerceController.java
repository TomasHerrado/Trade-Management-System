package com.tsm.api.controller;

import com.tsm.api.dto.request.CommerceRequest;
import com.tsm.api.dto.response.CommerceResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.CommerceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces")
@RequiredArgsConstructor
public class CommerceController {
    private final CommerceService commerceService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    // Solo OWNER puede crear comercios (queda implícito: el creador se convierte en OWNER)
    @PostMapping
    public ResponseEntity<CommerceResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommerceRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(201).body(commerceService.create(userId, request));
    }

    // Cualquier miembro puede listar los comercios a los que pertenece
    @GetMapping
    public ResponseEntity<List<CommerceResponse>> getMyCommerces(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(commerceService.getByUserId(userId));
    }

    // Cualquier miembro con acceso puede ver el comercio
    @GetMapping("/{id}")
    public ResponseEntity<CommerceResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, id);
        return ResponseEntity.ok(commerceService.getById(id));
    }

    // Solo OWNER puede editar su comercio
    @PutMapping("/{id}")
    public ResponseEntity<CommerceResponse> update(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommerceRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, id);
        return ResponseEntity.ok(commerceService.update(id, request));
    }

    // Solo OWNER puede desactivar su comercio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, id);
        commerceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}