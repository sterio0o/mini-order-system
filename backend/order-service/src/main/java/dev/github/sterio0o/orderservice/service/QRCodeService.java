package dev.github.sterio0o.orderservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final OrderService orderService;

    private final int WIDTH_QR_CODE = 400;
    private final int HEIGHT_QR_CODE = 400;

    // Генерирурет QR кода из текста и возвращает как массив байт (PNG)
    public byte[] generateQRCode(UUID orderId) {
        try {
            OrderResponseDto orderResponseDto = orderService.getOrderById(orderId);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(orderResponseDto.toString(), BarcodeFormat.QR_CODE, WIDTH_QR_CODE, HEIGHT_QR_CODE);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Генерирурет QR кода из текста и возвращает как Base64 строку
    public String generateQRCodeASBase64(UUID orderId) {
        byte[] qrCodeBytes = generateQRCode(orderId);
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }

}
