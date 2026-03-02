package com.aynur.payment.qrcode.controller;

import com.aynur.payment.common.exception.NotFoundException;
import com.aynur.payment.common.exception.UnauthorizedException;
import com.aynur.payment.common.util.CurrentUser;
import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.qrcode.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class QrCodeController {

    private final OrderRepository orderRepository;
    private final QrCodeService qrCodeService;

    @GetMapping("/{id}/qrcode.png")
    public ResponseEntity<byte[]> qrcode(@PathVariable Long id) {
        Long userId = CurrentUser.id();
        if (userId == null) throw new UnauthorizedException("Unauthorized");

        Order o = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!o.getUserId().equals(userId)) throw new UnauthorizedException("Forbidden");
        if (o.getStripeSessionUrl() == null) throw new NotFoundException("Stripe session url not found");

        byte[] png = qrCodeService.generatePng(o.getStripeSessionUrl(), 300);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noCache())
                .body(png);
    }
}