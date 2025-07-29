package com.fiap.pedido.gateway.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.MessageQueueException;
import com.fiap.pedido.exception.MessageSerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private OrderProducer orderProducer;

    private final String queueName = "test-queue";

    @BeforeEach
    void setUp() {
        orderProducer = new OrderProducer(rabbitTemplate, objectMapper);
        ReflectionTestUtils.setField(orderProducer, "queueName", queueName);
    }

    @Test
    void shouldSendOrderSuccessfully() throws Exception {
        Order order = createValidOrder();
        String orderJson = "{\"orderId\":\"123\"}";

        when(objectMapper.writeValueAsString(order)).thenReturn(orderJson);

        orderProducer.sendOrder(order);

        verify(objectMapper).writeValueAsString(order);
        verify(rabbitTemplate).convertAndSend(queueName, orderJson);
    }

    @Test
    void shouldThrowExceptionWhenSerializationFails() throws Exception {
        Order order = createValidOrder();
        when(objectMapper.writeValueAsString(order))
                .thenThrow(new RuntimeException("Serialization failed"));

        MessageSerializationException exception = assertThrows(MessageSerializationException.class,
                () -> orderProducer.sendOrder(order));

        assertEquals("Failed to serialize order to JSON", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void shouldThrowExceptionWhenRabbitMQConnectionFails() throws Exception {
        Order order = createValidOrder();
        String orderJson = "{\"orderId\":\"123\"}";

        when(objectMapper.writeValueAsString(order)).thenReturn(orderJson);
        doThrow(new AmqpException("Connection failed"))
                .when(rabbitTemplate).convertAndSend(eq(queueName), eq(orderJson));

        MessageQueueException exception = assertThrows(MessageQueueException.class,
                () -> orderProducer.sendOrder(order));

        assertEquals("Failed to connect to message queue", exception.getMessage());
        assertInstanceOf(AmqpException.class, exception.getCause());
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        order.setCardNumber("1234567890123456");
        order.setStatus("ABERTO");

        Item item = new Item();
        item.setSku("SKU123");
        item.setQuantity(2);
        order.setItems(List.of(item));

        return order;
    }
}
