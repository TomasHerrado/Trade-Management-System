package com.tsm.api.controller;
import com.tsm.api.dto.request.CashRegisterOpenRequest;
import com.tsm.api.dto.response.CashMovementResponse;
import com.tsm.api.dto.response.CashRegisterResponse;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.CashRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/branches/{branchId}/cash-register")
@RequiredArgsConstructor
public class CashRegisterController {
    private final CashRegisterService cashRegisterService;
    private final JwtService jwtService;

    @PostMapping("/open")
    public ResponseEntity<CashRegisterResponse> open(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CashRegisterOpenRequest request) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.status(201).body(cashRegisterService.open(branchId, userId, request));
    }

    @PostMapping("/close")
    public ResponseEntity<CashRegisterResponse> close(
            @PathVariable UUID branchId,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserId(authHeader.substring(7));
        return ResponseEntity.ok(cashRegisterService.close(branchId, userId));
    }

    @GetMapping("/current")
    public ResponseEntity<CashRegisterResponse> getCurrent(@PathVariable UUID branchId) {
        return ResponseEntity.ok(cashRegisterService.getOpenByBranchId(branchId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<CashRegisterResponse>> getHistory(@PathVariable UUID branchId) {
        return ResponseEntity.ok(cashRegisterService.getHistoryByBranchId(branchId));
    }

    @GetMapping("/{cashRegisterId}/movements")
    public ResponseEntity<List<CashMovementResponse>> getMovements(
            @PathVariable UUID cashRegisterId) {
        return ResponseEntity.ok(cashRegisterService.getMovements(cashRegisterId));
    }
}
