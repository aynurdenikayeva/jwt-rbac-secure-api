package com.aynur.payment.domain.repository;

import com.aynur.payment.domain.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrderId(Long orderId);
}