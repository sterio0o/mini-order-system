package dev.github.sterio0o.orderservice.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dev.github.sterio0o.orderservice.exception.PdfGenerateException;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderItem;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {
    private final TemplateEngine templateEngine;
    private final OrderService orderService;

    public byte[] generatePdf(UUID id) {
        Order order = orderService.getOrderEntityById(id);

        List<OrderItem> orderItemList = order.getOrderItems();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Context context = new Context();
            context.setVariable("order", order);

            String htmlContent = templateEngine.process("order-template", context);
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "/");

            builder.toStream(out);
            builder.run();

            return out.toByteArray();
        } catch (IOException e) {
            throw new PdfGenerateException("Ошибка генерации pdf");
        }
    }

}
