package com.fiap.pedido.mapper;

import com.fiap.pedido.controller.json.ItemDTO;
import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void shouldMapOrderDTOToOrder() {
        UUID customerId = UUID.randomUUID();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = customerId;
        orderDTO.cardNumber = "1234567890123456";
        orderDTO.items = List.of(
            new ItemDTO("SKU123", 2),
            new ItemDTO("SKU456", 1)
        );

        Order order = orderMapper.map(orderDTO);

        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals("1234567890123456", order.getCardNumber());
        assertEquals("ABERTO", order.getStatus());
        assertEquals(2, order.getItems().size());

        Item firstItem = order.getItems().get(0);
        assertEquals("SKU123", firstItem.getSku());
        assertEquals(2, firstItem.getQuantity());
    }

    @Test
    void shouldMapOrderToOrderDTO() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setCardNumber("9876543210987654");
        order.setStatus("PROCESSANDO");
        order.setItems(List.of(
            new Item("SKU789", 3),
            new Item("SKU012", 2)
        ));

        OrderDTO orderDTO = orderMapper.map(order);

        assertNotNull(orderDTO);
        assertEquals(orderId, orderDTO.orderId);
        assertEquals(customerId, orderDTO.customerId);
        assertEquals("9876543210987654", orderDTO.cardNumber);
        assertEquals("PROCESSANDO", orderDTO.status);
        assertEquals(2, orderDTO.items.size());

        ItemDTO firstItemDTO = orderDTO.items.get(0);
        assertEquals("SKU789", firstItemDTO.sku());
        assertEquals(3, firstItemDTO.quantity());
    }

    @Test
    void shouldHandleNullOrderDTO() {
        Order order = orderMapper.map((OrderDTO) null);

        assertNull(order);
    }

    @Test
    void shouldHandleNullOrder() {
        OrderDTO orderDTO = orderMapper.map((Order) null);

        assertNull(orderDTO);
    }

    @Test
    void shouldHandleEmptyItemsList() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "1111222233334444";
        orderDTO.items = List.of();

        Order order = orderMapper.map(orderDTO);

        assertNotNull(order);
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void shouldHandleNullItemsList() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "5555666677778888";
        orderDTO.items = null;

        Order order = orderMapper.map(orderDTO);

        assertNotNull(order);
        assertNull(order.getItems());
    }

    @Test
    void shouldGenerateUniqueOrderIds() {
        OrderDTO orderDTO1 = createBasicOrderDTO();
        OrderDTO orderDTO2 = createBasicOrderDTO();

        Order order1 = orderMapper.map(orderDTO1);
        Order order2 = orderMapper.map(orderDTO2);

        assertNotNull(order1.getOrderId());
        assertNotNull(order2.getOrderId());
        assertNotEquals(order1.getOrderId(), order2.getOrderId());
    }

    @Test
    void shouldAlwaysSetStatusToAberto() {
        OrderDTO orderDTO = createBasicOrderDTO();

        Order order = orderMapper.map(orderDTO);

        assertEquals("ABERTO", order.getStatus());
    }

    private OrderDTO createBasicOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "1234567890123456";
        orderDTO.items = List.of(new ItemDTO("SKU001", 1));
        return orderDTO;
    }
}
