package com.fiap.pedido.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void shouldCreateItemWithAllArgsConstructor() {
        String sku = "SKU123";
        Integer quantity = 5;

        Item item = new Item(sku, quantity);

        assertEquals(sku, item.getSku());
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    void shouldCreateItemWithNoArgsConstructor() {
        Item item = new Item();

        assertNotNull(item);
        assertNull(item.getSku());
        assertNull(item.getQuantity());
    }

    @Test
    void shouldSetAndGetSku() {
        Item item = new Item();
        String sku = "PRODUCT-456";

        item.setSku(sku);

        assertEquals(sku, item.getSku());
    }

    @Test
    void shouldSetAndGetQuantity() {
        Item item = new Item();
        Integer quantity = 10;

        item.setQuantity(quantity);

        assertEquals(quantity, item.getQuantity());
    }

    @Test
    void shouldHandleNullSku() {
        Item item = new Item();

        item.setSku(null);

        assertNull(item.getSku());
    }

    @Test
    void shouldHandleNullQuantity() {
        Item item = new Item();

        item.setQuantity(null);

        assertNull(item.getQuantity());
    }

    @Test
    void shouldHandleZeroQuantity() {
        Item item = new Item();

        item.setQuantity(0);

        assertEquals(0, item.getQuantity());
    }

    @Test
    void shouldHandleNegativeQuantity() {
        Item item = new Item();

        item.setQuantity(-1);

        assertEquals(-1, item.getQuantity());
    }
}
