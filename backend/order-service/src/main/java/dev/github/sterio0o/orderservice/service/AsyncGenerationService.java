package dev.github.sterio0o.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncGenerationService {
    private final PdfGenerationService pdfGenerationService;
    private final QRCodeService qrCodeService;

    // Метод для асинхронной генерации PDF отчета
    @Async
    public CompletableFuture<byte[]> generatePdfAsync(UUID id) {
        byte[] pdfReport = pdfGenerationService.generatePdf(id);
        return CompletableFuture.completedFuture(pdfReport);
    }

    // Метод для асинхронной генерации QR кода
    @Async
    public CompletableFuture<byte[]> generateQRCodeAsync(UUID id) {
        byte[] qrCode = qrCodeService.generateQRCode(id);
        return CompletableFuture.completedFuture(qrCode);
    }
}
