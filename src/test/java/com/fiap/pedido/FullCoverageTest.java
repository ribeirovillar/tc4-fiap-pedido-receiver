package com.fiap.pedido;

import com.fiap.pedido.controller.json.ItemDTO;
import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.mapper.OrderMapper;
import com.fiap.pedido.usecase.SendOrderUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class FullCoverageTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SendOrderUseCase sendOrderUseCase;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldCoverMapperAndUseCaseFlow() {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "1234567890123456";
        orderDTO.items = List.of(
            new ItemDTO("SKU123", 2),
            new ItemDTO("SKU456", 1)
        );

        Order order = orderMapper.map(orderDTO);

        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals("ABERTO", order.getStatus());
        assertEquals(2, order.getItems().size());

        Order processedOrder = sendOrderUseCase.execute(order);

        assertSame(order, processedOrder);

        OrderDTO resultDTO = orderMapper.map(processedOrder);

        assertNotNull(resultDTO);
        assertEquals(order.getOrderId(), resultDTO.orderId);
        assertEquals(order.getCustomerId(), resultDTO.customerId);
        assertEquals("ABERTO", resultDTO.status);
    }

    @Test
    void shouldCoverAllMapperScenarios() {
        assertNull(orderMapper.map((OrderDTO) null));
        assertNull(orderMapper.map((Order) null));

        OrderDTO emptyDTO = new OrderDTO();
        emptyDTO.customerId = UUID.randomUUID();
        emptyDTO.cardNumber = "1111222233334444";
        emptyDTO.items = List.of();

        Order emptyOrder = orderMapper.map(emptyDTO);
        assertNotNull(emptyOrder);
        assertTrue(emptyOrder.getItems().isEmpty());

        OrderDTO nullItemsDTO = new OrderDTO();
        nullItemsDTO.customerId = UUID.randomUUID();
        nullItemsDTO.cardNumber = "5555666677778888";
        nullItemsDTO.items = null;

        Order nullItemsOrder = orderMapper.map(nullItemsDTO);
        assertNotNull(nullItemsOrder);
        assertNull(nullItemsOrder.getItems());
    }

    @Test
    void shouldCoverDomainClasses() {
        Order order1 = new Order();
        assertNull(order1.getOrderId());

        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<Item> items = List.of(new Item("SKU001", 5));

        Order order2 = new Order(orderId, customerId, "1234", "ABERTO", items);
        assertEquals(orderId, order2.getOrderId());
        assertEquals(customerId, order2.getCustomerId());

        Item item1 = new Item();
        assertNull(item1.getSku());

        Item item2 = new Item("SKU999", 10);
        assertEquals("SKU999", item2.getSku());
        assertEquals(10, item2.getQuantity());

        item1.setSku("TEST-SKU");
        item1.setQuantity(3);
        assertEquals("TEST-SKU", item1.getSku());
        assertEquals(3, item1.getQuantity());
    }

    @Test
    void shouldTestMultipleMapperCalls() {
        OrderDTO dto1 = createBasicOrderDTO();
        OrderDTO dto2 = createBasicOrderDTO();

        Order order1 = orderMapper.map(dto1);
        Order order2 = orderMapper.map(dto2);

        assertNotEquals(order1.getOrderId(), order2.getOrderId());
        assertEquals("ABERTO", order1.getStatus());
        assertEquals("ABERTO", order2.getStatus());
    }

    private OrderDTO createBasicOrderDTO() {
        OrderDTO dto = new OrderDTO();
        dto.customerId = UUID.randomUUID();
        dto.cardNumber = "1234567890123456";
        dto.items = List.of(new ItemDTO("SKU001", 1));
        return dto;
    }
}
