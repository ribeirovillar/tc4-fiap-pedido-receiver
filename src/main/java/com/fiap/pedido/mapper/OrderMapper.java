package com.fiap.pedido.mapper;

import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "status", constant = "ABERTO")
    Order map(OrderDTO orderDTO);

    OrderDTO map(Order order);

}
