package com.tsm.api.controller;

import com.tsm.api.dto.request.UserCommerceRequest;
import com.tsm.api.dto.response.UserCommerceResponse;
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

    @PostMapping
    public ResponseEntity<UserCommerceResponse> addUser(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserCommerceRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.status(201).body(userCommerceService.addUser(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<UserCommerceResponse>> getUsers(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        return ResponseEntity.ok(userCommerceService.getByCommerceId(commerceId));
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> removeUser(
            @PathVariable UUID commerceId,
            @PathVariable UUID targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateCommerceAccess(userId, commerceId);
        userCommerceService.removeUser(commerceId, targetUserId);
        return ResponseEntity.noContent().build();
    }

}
