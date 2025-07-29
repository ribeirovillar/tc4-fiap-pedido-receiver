package com.fiap.pedido.controller.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {

    @JsonProperty(value = "orderId", access = JsonProperty.Access.READ_ONLY)
    public UUID orderId;

    @JsonProperty("customerId")
    public UUID customerId;

    @JsonProperty("items")
    public List<ItemDTO> items;

    @JsonProperty("cardNumber")
    public String cardNumber;

    @JsonProperty(value = "status", access = JsonProperty.Access.READ_ONLY)
    public String status;

}
