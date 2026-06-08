package com.tsm.api.controller;

import com.tsm.api.dto.request.UserCommerceRequest;
import com.tsm.api.dto.response.UserCommerceResponse;
import com.tsm.api.entity.UserRole;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.UserCommerceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/users")
@RequiredArgsConstructor
public class UserCommerceController {

    private final UserCommerceService userCommerceService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    // Solo OWNER puede agregar usuarios a su comercio.
    // Además no se puede asignar el rol OWNER mediante este endpoint.
    @PostMapping
    public ResponseEntity<UserCommerceResponse> addUser(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserCommerceRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        if (request.getRole() == UserRole.OWNER) {
            throw new BusinessException("No se puede asignar el rol de propietario");
        }
        return ResponseEntity.status(201).body(userCommerceService.addUser(commerceId, request));
    }

    // Solo OWNER y ADMIN pueden ver la lista de usuarios del comercio
    @GetMapping
    public ResponseEntity<List<UserCommerceResponse>> getUsers(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwnerOrAdmin(userId, commerceId);
        return ResponseEntity.ok(userCommerceService.getByCommerceId(commerceId));
    }

    // Solo OWNER puede eliminar usuarios del comercio
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> removeUser(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        userCommerceService.removeUser(commerceId, targetUserId);
        return ResponseEntity.noContent().build();
    }
}