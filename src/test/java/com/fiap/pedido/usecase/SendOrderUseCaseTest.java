package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Item;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    private SendOrderUseCase sendOrderUseCase;

    @BeforeEach
    void setUp() {
        sendOrderUseCase = new SendOrderUseCase(orderGateway);
    }

    @Test
    void shouldExecuteOrderSendingSuccessfully() {
        Order order = createValidOrder();

        Order result = sendOrderUseCase.execute(order);

        verify(orderGateway, times(1)).sendOrder(order);
        assertSame(order, result);
        assertNotNull(result.getOrderId());
        assertEquals("ABERTO", result.getStatus());
    }

    @Test
    void shouldPassOrderToGatewayWithAllFields() {
        Order order = createValidOrder();
        UUID expectedOrderId = order.getOrderId();
        UUID expectedCustomerId = order.getCustomerId();

        Order result = sendOrderUseCase.execute(order);

        verify(orderGateway).sendOrder(argThat(sentOrder ->
            sentOrder.getOrderId().equals(expectedOrderId) &&
            sentOrder.getCustomerId().equals(expectedCustomerId) &&
            sentOrder.getItems().size() == 2 &&
            sentOrder.getCardNumber().equals("1234567890123456")
        ));
        assertEquals(order, result);
    }

    @Test
    void shouldReturnSameOrderInstanceAfterSending() {
        Order order = createValidOrder();

        Order result = sendOrderUseCase.execute(order);

        assertSame(order, result);
        verify(orderGateway, times(1)).sendOrder(order);
    }

    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(UUID.randomUUID());
        order.setCardNumber("1234567890123456");
        order.setStatus("ABERTO");

        Item item1 = new Item("SKU123", 2);
        Item item2 = new Item("SKU456", 1);
        order.setItems(List.of(item1, item2));

        return order;
    }
}
