package com.fiap.pedido.gateway.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.MessageQueueException;
import com.fiap.pedido.exception.MessageSerializationException;
import com.fiap.pedido.gateway.OrderGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderProducer implements OrderGateway {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OrderProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendOrder(Order order) {
        try {
            String orderJson = serializeOrder(order);
            sendToQueue(orderJson, order.getOrderId());
        } catch (AmqpException e) {
            log.error("AMQP connection error when sending order {}: {}", order.getOrderId(), e.getMessage(), e);
            throw new MessageQueueException("Failed to connect to message queue", e);
        } catch (MessageSerializationException e) {
            log.error("Serialization error when sending order {}: {}", order.getOrderId(), e.getMessage(), e);
            throw new MessageSerializationException("Failed to serialize order to JSON", e);
        } catch (Exception e) {
            log.error("Unexpected error when sending order {} to queue: {}", order.getOrderId(), e.getMessage(), e);
            throw new MessageQueueException("Failed to send order to message queue", e);
        }
    }

    private String serializeOrder(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            log.debug("Successfully serialized order {} to JSON", order.getOrderId());
            return orderJson;
        } catch (Exception e) {
            throw new MessageSerializationException(e);
        }
    }

    private void sendToQueue(String orderJson, java.util.UUID orderId) {
        try {
            log.info("Sending order {} to queue {}", orderId, queueName);
            rabbitTemplate.convertAndSend(queueName, orderJson);
            log.info("Order {} sent successfully to queue", orderId);
        } catch (AmqpException e) {
            log.error("Failed to send order {} to RabbitMQ queue {}: {}", orderId, queueName, e.getMessage());
            throw e;
        }
    }
}
