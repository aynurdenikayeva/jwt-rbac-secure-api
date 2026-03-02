package com.aynur.payment.order.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderHistoryResponse {
    private List<OrderResponse> orders;
}