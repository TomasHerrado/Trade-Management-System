package com.tsm.api.controller;
import com.tsm.api.dto.request.SaleRequest;
import com.tsm.api.dto.response.SaleResponse;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches/{branchId}/sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<SaleResponse> create(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SaleRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(201).body(saleService.create(branchId, userId, request));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(saleService.getByBranchId(branchId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(saleService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SaleResponse>> getByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(saleService.getByCustomerId(customerId));
    }
}
