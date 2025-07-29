package com.fiap.pedido.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order implements Serializable {
    UUID orderId;
    UUID customerId;
    String cardNumber;
    String status;
    List<Item> items;
}
