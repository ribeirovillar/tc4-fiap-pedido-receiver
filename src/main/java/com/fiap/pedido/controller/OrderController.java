package com.fiap.pedido.controller;

import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.mapper.OrderMapper;
import com.fiap.pedido.usecase.SendOrderUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orders")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderController {

    SendOrderUseCase sendOrderUseCase;
    OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderMapper.map(sendOrderUseCase.execute(orderMapper.map(orderDTO))));
    }

}
