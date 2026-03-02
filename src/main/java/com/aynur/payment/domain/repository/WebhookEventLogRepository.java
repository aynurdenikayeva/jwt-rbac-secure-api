package com.aynur.payment.domain.repository;

import com.aynur.payment.domain.entity.WebhookEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WebhookEventLogRepository extends JpaRepository<WebhookEventLog, Long> {
    Optional<WebhookEventLog> findByStripeEventId(String stripeEventId);
}