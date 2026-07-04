package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.QRCodeGenerationException;
import dev.github.sterio0o.orderservice.model.dto.OrderItemResponseDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private QRCodeService qrCodeService;

    private UUID userId;
    private UUID orderId;
    private OrderResponseDto orderResponseDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        OrderItemResponseDto item = new OrderItemResponseDto(
                "MacBook",
                1,
                BigDecimal.valueOf(2000)
        );

        orderResponseDto = new OrderResponseDto(
                orderId,
                "test@example.com",
                List.of(item),
                BigDecimal.valueOf(2000),
                OrderStatus.PENDING_PAYMENT,
                LocalDateTime.now()
        );
    }

    // GENERATE QR CODE

    @Test
    void generateQRCode_Success_ReturnByteArray() {
        when(orderService.getOrderById(orderId)).thenReturn(orderResponseDto);

        byte[] qrCodeBytes = qrCodeService.generateQRCode(orderId);

        assertNotNull(qrCodeBytes);
        assertTrue(qrCodeBytes.length > 0);
        verify(orderService).getOrderById(orderId);
    }

    @Test
    void generateQRCode_WhenOrderNotFound_ThrowsOrderNotFoundException() {
        when(orderService.getOrderById(orderId)).thenThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> qrCodeService.generateQRCode(orderId));

        verify(orderService).getOrderById(orderId);
    }

    // GENERATE QR CODE AS BASE 64

    @Test
    void generateQRCodeASBase64_Success_ReturnBase64String() {
        when(orderService.getOrderById(orderId)).thenReturn(orderResponseDto);

        String result = qrCodeService.generateQRCodeASBase64(orderId);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertDoesNotThrow(() -> Base64.getDecoder().decode(result));
        verify(orderService).getOrderById(orderId);
    }
}
