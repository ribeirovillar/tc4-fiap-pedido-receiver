package com.fiap.pedido.usecase;

import com.fiap.pedido.domain.Order;
import com.fiap.pedido.gateway.OrderGateway;
import org.springframework.stereotype.Component;

@Component
public class SendOrderUseCase {
    private final OrderGateway orderGateway;

    public SendOrderUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public Order execute(Order order) {
        orderGateway.sendOrder(order);
        return order;
    }
}
