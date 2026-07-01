package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.ProductNotFoundException;
import dev.github.sterio0o.orderservice.kafka.KafkaProducer;
import dev.github.sterio0o.orderservice.model.dto.OrderItemRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderItem;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.model.entities.Product;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import dev.github.sterio0o.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID orderId;
    private UUID productId;
    private Product product;
    private Order order;
    private OrderRequestDto orderRequestDto;
    private String customerEmail;

    // Инициализация данных
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();
        customerEmail = "test@example.com";

        product = new Product();
        product.setId(productId);
        product.setProductName("MacBook");
        product.setPrice(2000);

        OrderItem orderItem = new OrderItem(UUID.randomUUID(), order, product, 1,  BigDecimal.valueOf(2000));

        order = Order.builder()
                .id(orderId)
                .userId(userId)
                .customerEmail(customerEmail)
                .status(OrderStatus.PENDING_PAYMENT)
                .orderItems(List.of(orderItem))
                .amount(BigDecimal.valueOf(2000))
                .createdAt(LocalDateTime.now())
                .build();

        orderRequestDto = new OrderRequestDto(
                customerEmail,
                List.of(new OrderItemRequestDto(UUID.randomUUID(), "MacBook", 1))
        );
    }

    // GET ORDER BY ID TESTS

    @Test
    void getOrderById_OrderNotFoundException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException expectedException = assertThrows(OrderNotFoundException.class, () ->
                orderService.getOrderById(orderId)
        );

        String expectedMessage = "Order with ID=" + orderId + " not found";
        assertEquals(expectedMessage, expectedException.getMessage());

        verify(orderRepository).findById(orderId); // проверяет что был вызван метод репозитория
        verifyNoMoreInteractions(orderRepository); // проверяет что нет скрытых запросов к БД
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDto result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.id());
        assertEquals(customerEmail, result.customerEmail());
        assertEquals(1, result.items().size());
        assertEquals(BigDecimal.valueOf(2000), result.amount());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.status());
        assertEquals(product.getProductName(), result.items().get(0).productName());

        verify(orderRepository).findById(orderId); // проверяет что был вызван метод репозитория
        verifyNoMoreInteractions(orderRepository); // проверяет что нет скрытых запросов к БД
    }

    // GET ALL ORDERS (Pagination)

    @Test
    void getAllOrders_ReturnPage_OrderResponseDto() {
        Pageable pageable = Pageable.unpaged();
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        Page<OrderResponseDto> result = orderService.getAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderId, result.getContent().get(0).id());

        verify(orderRepository).findAll(pageable);
        verifyNoMoreInteractions(orderRepository);
    }

    // GET MY ORDERS (Pagination)

    @Test
    void getMyOrders() {
        Pageable pageable = Pageable.unpaged();
        Page<Order> orderPage = new PageImpl<>(List.of(order));
        when(orderRepository.findAllByUserId(userId, pageable)).thenReturn(orderPage);

        Page<OrderResponseDto> result = orderService.getMyOrders(pageable, userId);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderId, result.getContent().get(0).id());

        verify(orderRepository).findAllByUserId(userId, pageable);
        verifyNoMoreInteractions(orderRepository);
    }

    // CREATE ORDER

    @Test
    void createOrder_ProductNotFoundException() {
        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.empty());
        ProductNotFoundException expectedException = assertThrows(ProductNotFoundException.class, () ->
                orderService.createOrder(orderRequestDto, userId));

        String expectedMessage = "Product with name=" + product.getProductName() + " not found";
        assertEquals(expectedMessage, expectedException.getMessage());

        verify(productRepository).findByProductName(product.getProductName()); // Проверка, что метод искал продукт
        verifyNoInteractions(orderRepository); // Проверка, что не вызывал orderRepository
        verifyNoInteractions(kafkaProducer); // Проверка, что не отправил ничего в Kafka
    }

    @Test
    void createOrder_Success() {
        BigDecimal expectedTotalAmount = BigDecimal.valueOf(2000);

        when(productRepository.findByProductName(product.getProductName())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto result = orderService.createOrder(orderRequestDto, userId);

        assertNotNull(result);
        assertEquals(orderId, result.id());
        assertEquals(order.getCustomerEmail(), result.customerEmail());
        assertEquals(expectedTotalAmount, result.amount());
        assertEquals(OrderStatus.PENDING_PAYMENT, result.status());

        verify(productRepository).findByProductName(product.getProductName());
        verify(orderRepository).save(any(Order.class));
        // Проверка, что в Kafka улетело правильное событие
        verify(kafkaProducer).sendEvent(argThat(event ->
            event.orderId().equals(orderId) &&
            event.userId().equals(userId) &&
            event.customerEmail().equals(customerEmail) &&
            event.amount().compareTo(expectedTotalAmount) == 0
        ));

        // Проверка, что не было лишних вызовов
        verifyNoMoreInteractions(productRepository, orderRepository, kafkaProducer);
    }

    @Test
    void createOrder_MultipleItems_CalculateTotalAmountCorrect() {
        Product p1 = new Product();
        p1.setId(UUID.randomUUID());
        p1.setProductName("MacBook");
        p1.setPrice(2000);
        Product p2 = new Product();
        p2.setId(UUID.randomUUID());
        p2.setProductName("Mouse");
        p2.setPrice(50);

        OrderRequestDto multiRequestDto = new OrderRequestDto(customerEmail, List.of(
                new OrderItemRequestDto(UUID.randomUUID(), "MacBook", 2),
                new OrderItemRequestDto(UUID.randomUUID(), "Mouse", 3)
        ));

        when(productRepository.findByProductName("MacBook")).thenReturn(Optional.of(p1));
        when(productRepository.findByProductName("Mouse")).thenReturn(Optional.of(p2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        OrderResponseDto result = orderService.createOrder(multiRequestDto, userId);

        assertEquals(BigDecimal.valueOf(4150), result.amount());
    }

    // DELETE ORDER

    @Test
    void deleteOrder_Success() {
        when(orderRepository.existsById(orderId)).thenReturn(true);
        orderService.deleteOrder(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteOrder_OrderNotFoundException() {
        UUID fakeUUID = UUID.randomUUID();
        when(orderRepository.existsById(fakeUUID)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(fakeUUID));
        verify(orderRepository, never()).deleteById(any());
    }

}