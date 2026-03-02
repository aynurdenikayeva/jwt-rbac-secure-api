package com.aynur.payment.order.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private long amountMinor;
    private String currency;
    private String description;

    private String status;

    private String stripeSessionUrl;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}