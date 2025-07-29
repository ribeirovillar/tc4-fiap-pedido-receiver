package com.fiap.pedido.exception;

public class MessageSerializationException extends RuntimeException {

    public MessageSerializationException(Throwable throwable) {
        super(throwable);
    }

    public MessageSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
