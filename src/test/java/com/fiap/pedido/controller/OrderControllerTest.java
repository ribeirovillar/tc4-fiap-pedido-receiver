package com.fiap.pedido.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.json.ItemDTO;
import com.fiap.pedido.controller.json.OrderDTO;
import com.fiap.pedido.domain.Order;
import com.fiap.pedido.exception.MessageQueueException;
import com.fiap.pedido.mapper.OrderMapper;
import com.fiap.pedido.usecase.SendOrderUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SendOrderUseCase sendOrderUseCase;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        OrderDTO requestDto = new OrderDTO();
        requestDto.customerId = customerId;
        requestDto.cardNumber = "1234567890123456";
        requestDto.items = List.of(new ItemDTO("SKU123", 2));

        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setStatus("ABERTO");

        OrderDTO responseDto = new OrderDTO();
        responseDto.orderId = orderId;
        responseDto.customerId = customerId;
        responseDto.status = "ABERTO";

        when(orderMapper.map(any(OrderDTO.class))).thenReturn(order);
        when(sendOrderUseCase.execute(any(Order.class))).thenReturn(order);
        when(orderMapper.map(any(Order.class))).thenReturn(responseDto);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("ABERTO"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenRabbitMQFails() throws Exception {
        UUID customerId = UUID.randomUUID();

        OrderDTO requestDto = new OrderDTO();
        requestDto.customerId = customerId;
        requestDto.cardNumber = "1234567890123456";
        requestDto.items = List.of(new ItemDTO("SKU123", 2));

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setCustomerId(customerId);

        when(orderMapper.map(any(OrderDTO.class))).thenReturn(order);
        when(sendOrderUseCase.execute(any(Order.class)))
                .thenThrow(new MessageQueueException("Failed to connect to message queue"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Message Queue Error"))
                .andExpect(jsonPath("$.detail").value("Failed to connect to message queue service"));
    }

}