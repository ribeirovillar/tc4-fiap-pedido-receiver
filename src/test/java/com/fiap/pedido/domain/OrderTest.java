package com.fiap.pedido.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithAllArgsConstructor() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String cardNumber = "1234567890123456";
        String status = "ABERTO";
        List<Item> items = List.of(new Item("SKU123", 2));

        Order order = new Order(orderId, customerId, cardNumber, status, items);

        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(cardNumber, order.getCardNumber());
        assertEquals(status, order.getStatus());
        assertEquals(items, order.getItems());
    }

    @Test
    void shouldCreateOrderWithNoArgsConstructor() {
        Order order = new Order();

        assertNotNull(order);
        assertNull(order.getOrderId());
        assertNull(order.getCustomerId());
        assertNull(order.getCardNumber());
        assertNull(order.getStatus());
        assertNull(order.getItems());
    }

    @Test
    void shouldSetAndGetAllFields() {
        Order order = new Order();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String cardNumber = "9876543210987654";
        String status = "PROCESSANDO";
        List<Item> items = new ArrayList<>();
        items.add(new Item("SKU456", 3));

        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setCardNumber(cardNumber);
        order.setStatus(status);
        order.setItems(items);

        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(cardNumber, order.getCardNumber());
        assertEquals(status, order.getStatus());
        assertEquals(items, order.getItems());
        assertEquals(1, order.getItems().size());
    }

    @Test
    void shouldHandleNullValues() {
        Order order = new Order();

        order.setOrderId(null);
        order.setCustomerId(null);
        order.setCardNumber(null);
        order.setStatus(null);
        order.setItems(null);

        assertNull(order.getOrderId());
        assertNull(order.getCustomerId());
        assertNull(order.getCardNumber());
        assertNull(order.getStatus());
        assertNull(order.getItems());
    }

    @Test
    void shouldModifyItemsList() {
        Order order = new Order();
        List<Item> items = new ArrayList<>();
        order.setItems(items);

        items.add(new Item("SKU001", 1));
        items.add(new Item("SKU002", 2));

        assertEquals(2, order.getItems().size());
        assertEquals("SKU001", order.getItems().get(0).getSku());
        assertEquals("SKU002", order.getItems().get(1).getSku());
    }
}
