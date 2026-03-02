package com.aynur.payment.order.controller;

import com.aynur.payment.common.response.ApiResponse;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;

    @GetMapping
    public ApiResponse<Object> all() {
        var list = orderRepository.findAll().stream().map(o ->
                OrderResponse.builder()
                        .id(o.getId())
                        .amountMinor(o.getAmountMinor())
                        .currency(o.getCurrency())
                        .description(o.getDescription())
                        .status(o.getStatus())
                        .stripeSessionUrl(o.getStripeSessionUrl())
                        .createdAt(o.getCreatedAt())
                        .completedAt(o.getCompletedAt())
                        .build()
        ).collect(Collectors.toList());

        return ApiResponse.ok(list);
    }
}