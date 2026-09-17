package dev.github.sterio0o.orderservice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dev.github.sterio0o.orderservice.exception.PdfGenerateException;
import dev.github.sterio0o.orderservice.model.entities.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {
    private final TemplateEngine templateEngine;
    private final OrderService orderService;

    // Генерирует PDF из HTML страницы order_template
    public byte[] generatePdf(UUID id) {
        Order order = orderService.getOrderEntityById(id);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Context context = new Context();
            context.setVariable("order", order);

            // Создание HTML
            String htmlContent = templateEngine.process("order-template", context);

            // Превращение HTML в PDF
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useFont(
                    () -> getClass().getResourceAsStream("/fonts/arialmt.ttf"),
                    "Arial"
            );
            builder.withHtmlContent(htmlContent, null);

            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerateException("Ошибка генерации pdf", e);
        }
    }

}
