package com.tsm.api.service;

public interface PasswordResetService {
    void forgotPassword(String email);
    void verifyCode(String email, String code);
    void resetPassword(String email, String code, String newPassword);
}