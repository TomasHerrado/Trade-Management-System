package com.tsm.api.controller;

import com.tsm.api.dto.request.InviteUserRequest;
import com.tsm.api.dto.response.UserCommerceDetailResponse;
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

    @PostMapping("/invite")
    public ResponseEntity<UserCommerceDetailResponse> invite(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody InviteUserRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(201)
                .body(teamService.invite(commerceId, userId, request));
    }

    @GetMapping
    public ResponseEntity<List<UserCommerceDetailResponse>> getTeam(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        // cualquier miembro puede ver el equipo
        return ResponseEntity.ok(teamService.getTeam(commerceId));
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> remove(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        teamService.remove(commerceId, targetUserId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{targetUserId}/branches/{branchId}")
    public ResponseEntity<UserCommerceDetailResponse> assignBranch(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(
                teamService.assignBranch(commerceId, targetUserId, branchId, userId));
    }

    @DeleteMapping("/{targetUserId}/branches/{branchId}")
    public ResponseEntity<Void> unassignBranch(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        teamService.unassignBranch(commerceId, targetUserId, branchId, userId);
        return ResponseEntity.noContent().build();
    }
}