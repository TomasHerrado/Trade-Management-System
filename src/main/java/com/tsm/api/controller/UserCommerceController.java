package com.tsm.api.controller;
import com.tsm.api.dto.request.UserCommerceRequest;
import com.tsm.api.dto.response.UserCommerceResponse;
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

    @PostMapping
    public ResponseEntity<UserCommerceResponse> addUser(
            @PathVariable UUID commerceId,
            @Valid @RequestBody UserCommerceRequest request) {
        return ResponseEntity.status(201).body(userCommerceService.addUser(commerceId, request));
    }

    @GetMapping
    public ResponseEntity<List<UserCommerceResponse>> getUsers(@PathVariable UUID commerceId) {
        return ResponseEntity.ok(userCommerceService.getByCommerceId(commerceId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(
            @PathVariable UUID commerceId,
            @PathVariable UUID userId) {
        userCommerceService.removeUser(commerceId, userId);
        return ResponseEntity.noContent().build();
    }
}
