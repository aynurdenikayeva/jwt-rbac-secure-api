package com.aynur.payment.order.service;

import com.aynur.payment.common.exception.NotFoundException;
import com.aynur.payment.common.exception.UnauthorizedException;
import com.aynur.payment.common.util.CurrentUser;
import com.aynur.payment.common.util.DateUtil;
import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.order.dto.*;
import com.aynur.payment.payment.stripe.StripeCheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StripeCheckoutService stripeCheckoutService;

    @Override
    public OrderResponse create(CreateOrderRequest req) {
        Long userId = CurrentUser.id();
        if (userId == null) throw new UnauthorizedException("Unauthorized");

        Order o = Order.builder()
                .userId(userId)
                .amountMinor(req.getAmountMinor())
                .currency(req.getCurrency().toLowerCase())
                .description(req.getDescription())
                .status("PENDING")
                .createdAt(DateUtil.now())
                .build();

        o = orderRepository.save(o);

        // stripe session create
        var session = stripeCheckoutService.createCheckoutSession(o);

        o.setStripeSessionId(session.sessionId());
        o.setStripeSessionUrl(session.sessionUrl());
        o = orderRepository.save(o);

        return toResponse(o);
    }

    @Override
    public OrderHistoryResponse myHistory() {
        Long userId = CurrentUser.id();
        if (userId == null) throw new UnauthorizedException("Unauthorized");

        var list = orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());

        return OrderHistoryResponse.builder().orders(list).build();
    }

    @Override
    public OrderResponse getById(Long id) {
        Long userId = CurrentUser.id();
        if (userId == null) throw new UnauthorizedException("Unauthorized");

        Order o = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // owner check (admin-ı ayrıca controller-də açırıq)
        if (!o.getUserId().equals(userId)) {
            throw new UnauthorizedException("Forbidden");
        }
        return toResponse(o);
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .amountMinor(o.getAmountMinor())
                .currency(o.getCurrency())
                .description(o.getDescription())
                .status(o.getStatus())
                .stripeSessionUrl(o.getStripeSessionUrl())
                .createdAt(o.getCreatedAt())
                .completedAt(o.getCompletedAt())
                .build();
    }
}