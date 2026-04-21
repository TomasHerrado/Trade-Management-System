package com.tsm.api.controller;
import com.tsm.api.dto.request.BranchRequest;
import com.tsm.api.dto.response.BranchResponse;
import com.tsm.api.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<BranchResponse> create(
            @PathVariable UUID commerceId,
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.status(201).body(branchService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getByCommerce(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(branchService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(branchService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody BranchRequest request) {
        return ResponseEntity.ok(branchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        branchService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
