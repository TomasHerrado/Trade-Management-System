package com.tsm.api.controller;

import com.tsm.api.dto.request.InviteUserRequest;
import com.tsm.api.dto.response.UserCommerceDetailResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    // Solo OWNER puede invitar usuarios al comercio
    @PostMapping("/invite")
    public ResponseEntity<UserCommerceDetailResponse> invite(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody InviteUserRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        // La validación de OWNER ya ocurre dentro de TeamService.invite(),
        // pero la duplicamos aquí para fallar rápido antes de llamar al servicio.
        authorizationService.validateOwner(userId, commerceId);
        return ResponseEntity.status(201)
                .body(teamService.invite(commerceId, userId, request));
    }

    // Cualquier miembro del comercio puede ver el equipo
    @GetMapping
    public ResponseEntity<List<UserCommerceDetailResponse>> getTeam(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(teamService.getTeam(commerceId));
    }

    // Solo OWNER puede eliminar miembros del equipo
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> remove(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        teamService.remove(commerceId, targetUserId, userId);
        return ResponseEntity.noContent().build();
    }

    // Solo OWNER puede asignar sucursales a empleados
    @PostMapping("/{targetUserId}/branches/{branchId}")
    public ResponseEntity<UserCommerceDetailResponse> assignBranch(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        return ResponseEntity.ok(
                teamService.assignBranch(commerceId, targetUserId, branchId, userId));
    }

    // Solo OWNER puede desasignar sucursales
    @DeleteMapping("/{targetUserId}/branches/{branchId}")
    public ResponseEntity<Void> unassignBranch(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        teamService.unassignBranch(commerceId, targetUserId, branchId, userId);
        return ResponseEntity.noContent().build();
    }
}