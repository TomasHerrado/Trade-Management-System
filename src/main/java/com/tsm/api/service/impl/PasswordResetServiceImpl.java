package com.tsm.api.service.impl;

import com.tsm.api.entity.PasswordResetCode;
import com.tsm.api.entity.User;
import com.tsm.api.exception.BusinessException;
import com.tsm.api.repository.PasswordResetCodeRepository;
import com.tsm.api.repository.UserRepository;
import com.tsm.api.service.EmailService;
import com.tsm.api.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetCodeRepository resetCodeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.code-expiration-minutes}")
    private int codeExpirationMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // No revelamos si el email existe o no, para no filtrar información de cuentas.
        userRepository.findByEmail(email).ifPresent(user -> {
            String code = generateCode();

            PasswordResetCode resetCode = PasswordResetCode.builder()
                    .email(email)
                    .code(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(codeExpirationMinutes))
                    .used(false)
                    .verified(false)
                    .build();

            resetCodeRepository.save(resetCode);
            emailService.sendPasswordResetCode(email, code);
        });
    }

    @Override
    @Transactional
    public void verifyCode(String email, String code) {
        PasswordResetCode resetCode = getValidCode(email, code);
        resetCode.setVerified(true);
        resetCodeRepository.save(resetCode);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        PasswordResetCode resetCode = getValidCode(email, code);

        if (!resetCode.isVerified()) {
            throw new BusinessException("El código no fue verificado");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetCode.setUsed(true);
        resetCodeRepository.save(resetCode);
    }

    private PasswordResetCode getValidCode(String email, String code) {
        PasswordResetCode resetCode = resetCodeRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BusinessException("No se encontró un código para este email"));

        if (!resetCode.getCode().equals(code)) {
            throw new BusinessException("Código incorrecto");
        }
        if (resetCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El código expiró, solicitá uno nuevo");
        }

        return resetCode;
    }

    private String generateCode() {
        int number = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(number);
    }
}