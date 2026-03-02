package com.aynur.payment.order.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateOrderRequest {

    @Min(1)
    private long amountMinor; // 1050 = 10.50

    @NotBlank
    private String currency; // usd, eur

    private String description;
}