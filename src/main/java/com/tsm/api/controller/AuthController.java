package com.tsm.api.controller;
import com.tsm.api.dto.request.ForgotPasswordRequest;
import com.tsm.api.dto.request.ResetPasswordRequest;
import com.tsm.api.dto.request.UserLoginRequest;
import com.tsm.api.dto.request.UserRegisterRequest;
import com.tsm.api.dto.request.VerifyResetCodeRequest;
import com.tsm.api.dto.response.AuthResponse;
import com.tsm.api.dto.response.MessageResponse;
import com.tsm.api.service.PasswordResetService;
import com.tsm.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(201).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Si el email está registrado, vas a recibir un código"));
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<MessageResponse> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        passwordResetService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new MessageResponse("Código verificado correctamente"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada correctamente"));
    }
}