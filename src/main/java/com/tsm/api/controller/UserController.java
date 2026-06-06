package com.tsm.api.controller;
import com.tsm.api.dto.request.UserUpdateRequest;
import com.tsm.api.dto.response.UserResponse;
import com.tsm.api.repository.UserRepository;
import com.tsm.api.security.JwtService;
import com.tsm.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tsm.api.entity.UserRole;
import com.tsm.api.repository.UserCommerceRepository;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        java.util.UUID userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> update(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UserUpdateRequest request) {
        String token = authHeader.substring(7);
        java.util.UUID userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(userService.update(userId, request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivate(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        java.util.UUID userId = jwtService.extractUserId(token);
        userService.deactivate(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/role")
    public ResponseEntity<Map<String, String>> getMyRole(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam UUID commerceId) {
        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);
        UserRole role = UserCommerceRepository
                .findByUserIdAndCommerceId(userId, commerceId)
                .map(uc -> uc.getRole())
                .orElse(null);
        if (role == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(Map.of("role", role.name()));
    }
}
