package com.fiap.pedido.gateway;

import com.fiap.pedido.domain.Order;

public interface OrderGateway {
    void sendOrder(Order order);
}
