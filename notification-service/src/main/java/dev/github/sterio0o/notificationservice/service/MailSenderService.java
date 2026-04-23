package dev.github.sterio0o.notificationservice.service;

import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void listener(PaymentProcessingEvent event) {
        log.info("Received payment event for order: {}", event.orderId());

        String to = event.email();
        String subject = createSubject(event);
        String body = createBody(event);

        sendEmail(to, subject, body);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(from);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        mailSender.send(mailMessage);
    }

    private String createSubject(PaymentProcessingEvent event) {
        return switch (event.paymentStatus()) {
            case "COMPLETED" -> "Payment successful - Order " + event.orderId();
            case "FAILED" -> "Payment failed - Order " + event.orderId();
            case "REFUNDED" -> "Payment refunded - Order " + event.orderId();
            default -> "Order " + event.orderId() + " updated";
        };
    }

    private String createBody(PaymentProcessingEvent event) {
        return switch (event.paymentStatus()) {
            case "COMPLETED" -> String.format("""
                    Дорогой пользователь,
                    
                    Ваша оплата $%.2f для заказа #%s успешно обработана.
                    
                    Payment ID: %s
                    Amount: $%.2f
                    
                    Благодарим Вас! 
                    """, event.amount(), event.orderId(), event.paymentId(), event.amount());

            case "FAILED" -> String.format("""
                    Дорогой пользователь,
                    
                    Возникла ошибка, ваша оплата $%.2f для заказа #%s не была обработана.
                    
                    Payment ID: %s
                    Amount: $%.2f
                    
                    Пожалуйста попробуйте снова.
                    """, event.amount(), event.orderId(), event.paymentId(), event.amount());

            case "REFUNDED" -> String.format("""
                    Дорогой пользователь,
                    
                    Ваша оплата $%.2f для заказа #%s была возвращена.
                    
                    Payment ID: %s
                    Amount: $%.2f
                    
                    Возврат средств произойдет в течении 1-2 рабочих дней.
                    """, event.amount(), event.orderId(), event.paymentId(), event.amount());

            default -> String.format("""
                    Дорогой пользователь,
                    
                    Ваш заказ #%s был обновлен
                    
                    Payment status: %s
                    Amount: $%.2f
                    """, event.orderId(), event.paymentId(), event.amount());
        };
    }
}
