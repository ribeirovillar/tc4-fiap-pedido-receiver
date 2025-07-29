package com.fiap.pedido.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "rabbitmq.queue.name=test-order-queue",
    "rabbitmq.queue.durable=true"
})
class RabbitMQConfigurationTest {

    @Autowired
    private RabbitMQConfiguration rabbitMQConfiguration;

    @Test
    void shouldCreateOrderQueueBean() {
        Queue queue = rabbitMQConfiguration.orderQueue();

        assertNotNull(queue);
        assertEquals("test-order-queue", queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void shouldCreateDurableQueue() {
        Queue queue = rabbitMQConfiguration.orderQueue();

        assertTrue(queue.isDurable());
        assertFalse(queue.isExclusive());
        assertFalse(queue.isAutoDelete());
    }
}

@SpringBootTest
@TestPropertySource(properties = {
    "rabbitmq.queue.name=temp-queue",
    "rabbitmq.queue.durable=false"
})
class RabbitMQConfigurationNonDurableTest {

    @Autowired
    private RabbitMQConfiguration rabbitMQConfiguration;

    @Test
    void shouldCreateNonDurableQueueWhenConfigured() {
        Queue queue = rabbitMQConfiguration.orderQueue();

        assertNotNull(queue);
        assertEquals("temp-queue", queue.getName());
        assertFalse(queue.isDurable());
    }
}
