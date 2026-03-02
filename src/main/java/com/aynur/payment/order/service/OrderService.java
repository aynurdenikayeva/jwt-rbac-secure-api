package com.aynur.payment.order.service;

import com.aynur.payment.order.dto.*;

public interface OrderService {
    OrderResponse create(CreateOrderRequest req);
    OrderHistoryResponse myHistory();
    OrderResponse getById(Long id);
}