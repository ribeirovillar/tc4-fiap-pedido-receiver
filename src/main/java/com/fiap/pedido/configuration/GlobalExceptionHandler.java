package com.fiap.pedido.configuration;

import com.fiap.pedido.exception.MessageQueueException;
import com.fiap.pedido.exception.MessageSerializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MessageSerializationException.class)
    public ResponseEntity<ProblemDetail> handleMessageSerializationException(MessageSerializationException ex) {
        log.error("Message serialization error occurred: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = buildProblemDetail("Message Serialization Error", HttpStatus.INTERNAL_SERVER_ERROR, "Failed to serialize message to JSON");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(MessageQueueException.class)
    public ResponseEntity<ProblemDetail> handleMessageQueueException(MessageQueueException ex) {
        log.error("Message queue error occurred: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = buildProblemDetail("Message Queue Error", HttpStatus.SERVICE_UNAVAILABLE, "Failed to connect to message queue service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ProblemDetail> handleAmqpException(AmqpException ex) {
        log.error("AMQP connection error occurred: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = buildProblemDetail("RabbitMQ Connection Error", HttpStatus.SERVICE_UNAVAILABLE, "Failed to connect to message queue service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred", ex);
        ProblemDetail problemDetail = buildProblemDetail("Unexpected Error", HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private ProblemDetail buildProblemDetail(String title, HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
