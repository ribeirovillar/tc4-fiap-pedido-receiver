package com.fiap.pedido.controller.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemDTO(
    @JsonProperty("sku") String sku,
    @JsonProperty("quantity") Integer quantity
) {
}
