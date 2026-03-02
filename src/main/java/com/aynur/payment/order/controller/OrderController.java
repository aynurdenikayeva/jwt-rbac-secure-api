package com.aynur.payment.order.controller;

import com.aynur.payment.common.response.ApiResponse;
import com.aynur.payment.order.dto.*;
import com.aynur.payment.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        return ApiResponse.ok(orderService.create(req));
    }

    @GetMapping("/my")
    public ApiResponse<OrderHistoryResponse> myHistory() {
        return ApiResponse.ok(orderService.myHistory());
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getById(id));
    }
}