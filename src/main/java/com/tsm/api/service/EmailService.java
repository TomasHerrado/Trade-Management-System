package com.tsm.api.service;

public interface EmailService {
    void sendPasswordResetCode(String toEmail, String code);
}