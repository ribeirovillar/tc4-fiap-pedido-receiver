package com.fiap.pedido;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.pedido.controller.json.ItemDTO;
import com.fiap.pedido.controller.json.OrderDTO;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateOrderEndToEnd() throws Exception {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "1234567890123456";
        orderDTO.items = List.of(
            new ItemDTO("SKU123", 2),
            new ItemDTO("SKU456", 1)
        );

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.customerId").value(orderDTO.customerId.toString()))
                .andExpect(jsonPath("$.status").value("ABERTO"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void shouldHandleEmptyItemsList() throws Exception {
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString());

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.customerId = UUID.randomUUID();
        orderDTO.cardNumber = "9876543210987654";
        orderDTO.items = List.of();

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated());
    }
}
