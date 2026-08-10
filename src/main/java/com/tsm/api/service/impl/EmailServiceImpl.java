package com.tsm.api.service.impl;

import com.tsm.api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetCode(String toEmail, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "TSM - Trade Management System");
            helper.setTo(toEmail);
            helper.setSubject("Tu código de recuperación de contraseña");
            helper.setText(buildHtmlContent(code), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("No se pudo enviar el email de recuperación", e);
        }
    }

    private String buildHtmlContent(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="margin:0; padding:0; background-color:#f9fafb; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f9fafb; padding: 40px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="480" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:16px; border:1px solid #f3f4f6; box-shadow: 0 1px 2px rgba(0,0,0,0.04); overflow:hidden;">
                      <tr>
                        <td style="padding: 40px 40px 24px 40px; text-align:center;">
                          <table role="presentation" cellpadding="0" cellspacing="0" style="margin:0 auto 16px auto;">
                            <tr>
                              <td width="48" height="48" style="background-color:#4f46e5; border-radius:12px; text-align:center; vertical-align:middle;">
                                <span style="color:#ffffff; font-size:20px; font-weight:700; line-height:48px;">T</span>
                              </td>
                            </tr>
                          </table>
                          <h1 style="margin:0; font-size:20px; color:#111827; font-weight:600;">TSM</h1>
                          <p style="margin:4px 0 0 0; font-size:13px; color:#6b7280;">Trade Management System</p>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding: 0 40px 40px 40px;">
                          <h2 style="margin:0 0 8px 0; font-size:17px; color:#111827; font-weight:600; text-align:center;">Recuperación de contraseña</h2>
                          <p style="margin:0 0 28px 0; font-size:14px; color:#6b7280; text-align:center; line-height:1.5;">
                            Usá el siguiente código para verificar tu identidad y crear una nueva contraseña. Expira en 10 minutos.
                          </p>
                          <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td style="background-color:#f9fafb; border:1px solid #e5e7eb; border-radius:12px; padding:20px; text-align:center;">
                                <span style="font-size:32px; font-weight:700; letter-spacing:8px; color:#4f46e5;">%s</span>
                              </td>
                            </tr>
                          </table>
                          <p style="margin:24px 0 0 0; font-size:12px; color:#9ca3af; text-align:center; line-height:1.5;">
                            Si vos no solicitaste este cambio, podés ignorar este mensaje de forma segura. Tu contraseña actual seguirá funcionando.
                          </p>
                        </td>
                      </tr>
                    </table>
                    <p style="margin:24px 0 0 0; font-size:12px; color:#9ca3af;">© 2026 TSM · Trade Management System</p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(code);
    }
}