package com.fiap.pedido.controller.exception;

import com.fiap.pedido.configuration.GlobalExceptionHandler;
import com.fiap.pedido.exception.MessageQueueException;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleMessageQueueException() {
        MessageQueueException exception = new MessageQueueException("RabbitMQ connection failed");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleMessageQueueException(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Message Queue Error", response.getBody().getTitle());
        assertEquals("Failed to connect to message queue service", response.getBody().getDetail());
    }

    @Test
    void shouldHandleAmqpException() {
        AmqpException exception = new AmqpException("AMQP connection timeout");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleAmqpException(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RabbitMQ Connection Error", response.getBody().getTitle());
        assertEquals("Failed to connect to message queue service", response.getBody().getDetail());
    }

    @Test
    void shouldHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Unexpected runtime error");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getTitle());
        assertEquals("An unexpected error occurred while processing your request", response.getBody().getDetail());
    }

    @Test
    void shouldHandleGenericException() {
        Exception exception = new Exception("Generic error");

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected Error", response.getBody().getTitle());
        assertEquals("An unexpected error occurred while processing your request", response.getBody().getDetail());
    }

    @Test
    void shouldHandleMessageQueueExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        MessageQueueException exception = new MessageQueueException("Queue error with cause", cause);

        ResponseEntity<ProblemDetail> response = exceptionHandler.handleMessageQueueException(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Message Queue Error", response.getBody().getTitle());
        assertEquals("Failed to connect to message queue service", response.getBody().getDetail());
    }
}
