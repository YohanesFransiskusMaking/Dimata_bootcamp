package service;

import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.Mail;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    @Transactional
    public void sendVerificationEmail(String to, String token) {

        String link = "http://localhost:8080/auth/verify?token=" + token;

        mailer.send(
                Mail.withText(
                        to,
                        "Verify Your Email",
                        "Click this link to verify your account:\n" + link));
    }

    public void sendResetPasswordEmail(String to, String token) {

        String link = "http://localhost:8080/auth/reset-password?token=" + token;

        mailer.send(
                Mail.withText(
                        to,
                        "Reset Password",
                        "Click this link to reset your password:\n" + link));
    }
}
