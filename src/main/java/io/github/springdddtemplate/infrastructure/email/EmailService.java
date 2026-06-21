package io.github.springdddtemplate.infrastructure.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/// Email service implementation using Spring's JavaMailSender.
/// Sends emails asynchronously via @Async to avoid blocking the request thread.
/// For production: replace SimpleMailMessage with MimeMessage for HTML templates,
/// and consider using a template engine (Thymeleaf/Freemarker) for email content.
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    /// Send welcome email to a newly registered user.
    /// Async execution prevents email sending from blocking the HTTP response.
    @Async
    public void sendWelcomeEmail(String toEmail, String username) {
        var message = new SimpleMailMessage();
        message.setFrom(mailProperties.from());
        message.setTo(toEmail);
        message.setSubject("Welcome to Spring DDD Template!");
        message.setText("""
                Hello %s,
                
                Welcome to our platform! Your account has been successfully created.
                
                Best regards,
                Spring DDD Template Team
                """.formatted(username));

        try {
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send welcome email to: {} - {}", toEmail, e.getMessage());
        }
    }

    /// Send notification email - generic email sending capability.
    @Async
    public void sendNotification(String toEmail, String subject, String body) {
        var message = new SimpleMailMessage();
        message.setFrom(mailProperties.from());
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            log.info("Notification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send notification email to: {} - {}", toEmail, e.getMessage());
        }
    }
}
