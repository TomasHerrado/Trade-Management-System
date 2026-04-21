package com.tsm.api.controller;
import com.tsm.api.dto.request.CategoryRequest;
import com.tsm.api.dto.response.CategoryResponse;
import com.tsm.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commerces/{commerceId}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @PathVariable UUID commerceId,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(201).body(categoryService.create(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getByCommerce(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(categoryService.getByCommerceId(commerceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
