package com.tsm.api.controller;
import com.tsm.api.dto.request.PaymentRequest;
import com.tsm.api.dto.request.SupplierRequest;
import com.tsm.api.dto.response.SupplierResponse;
import com.tsm.api.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<SupplierResponse> create(
            @PathVariable UUID commerceId,
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(201).body(supplierService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getByCommerce(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(supplierService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        supplierService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<SupplierResponse> registerPayment(
            @PathVariable UUID id,
            @RequestParam UUID branchId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(supplierService.registerPayment(id, branchId, request));
    }

    @GetMapping("/with-debt")
    public ResponseEntity<List<SupplierResponse>> getWithDebt(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(supplierService.getWithDebt(commerceId));
    }
}
