package com.aynur.payment.receipt.controller;

import com.aynur.payment.common.exception.NotFoundException;
import com.aynur.payment.common.exception.UnauthorizedException;
import com.aynur.payment.common.util.CurrentUser;
import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.entity.Receipt;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.domain.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class ReceiptController {
    private final OrderRepository orderRepository;
    private final ReceiptRepository receiptRepository;

    @GetMapping("/{id}/receipt.pdf")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Long userId = CurrentUser.id();
        if (userId == null) throw new UnauthorizedException("Unauthorized");
        Order o = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!o.getUserId().equals(userId)) throw new UnauthorizedException("Forbidden");
        if (!"COMPLETED".equals(o.getStatus())) throw new UnauthorizedException("Receipt available only for COMPLETED");

        Receipt r = receiptRepository.findByOrderId(id).orElseThrow(() -> new NotFoundException("Receipt not found"));
        Path p = Path.of(r.getFilePath());
        Resource resource = new FileSystemResource(p);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getFileName() + "\"")
                .body(resource);
    }
}