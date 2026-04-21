package com.tsm.api.controller;
import com.tsm.api.dto.request.CommerceRequest;
import com.tsm.api.dto.response.CommerceResponse;
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

    @PostMapping
    public ResponseEntity<CommerceResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CommerceRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(201).body(commerceService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<CommerceResponse>> getMyCommerces(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(commerceService.getByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommerceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(commerceService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommerceResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CommerceRequest request) {
        return ResponseEntity.ok(commerceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        commerceService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
