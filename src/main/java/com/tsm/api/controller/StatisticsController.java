package com.tsm.api.controller;

import com.tsm.api.dto.response.StatisticsResponse;
import com.tsm.api.security.AuthorizationService;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final JwtService jwtService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> get(
            @PathVariable UUID commerceId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        authorizationService.validateOwner(userId, commerceId);
        return ResponseEntity.ok(statisticsService.getByCommerceId(commerceId));
    }
}