package com.aynur.payment.payment.stripe;

import com.aynur.payment.common.util.DateUtil;
import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.entity.WebhookEventLog;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.domain.repository.WebhookEventLogRepository;
import com.aynur.payment.receipt.service.PdfReceiptService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {
    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    private final WebhookEventLogRepository eventLogRepository;
    private final OrderRepository orderRepository;
    private final PdfReceiptService pdfReceiptService;

    public void handle(String payload, String signatureHeader) {
        try {
            if (webhookSecret == null || webhookSecret.isBlank()) {
                throw new IllegalStateException("stripe.webhook-secret is missing (set STRIPE_WEBHOOK_SECRET env or stripe.webhook-secret in yml)");
            }
            Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
            log.info("Stripe webhook received. type={}, id={}", event.getType(), event.getId());

            if (eventLogRepository.findByStripeEventId(event.getId()).isPresent()) {
                log.info("Stripe webhook skipped as duplicate. id={}", event.getId());
                return;
            }
            eventLogRepository.save(WebhookEventLog.builder()
                    .stripeEventId(event.getId())
                    .type(event.getType())
                    .payloadSummary(payload.length() > 1000 ? payload.substring(0, 1000) : payload)
                    .createdAt(DateUtil.now())
                    .build());
            if (!"checkout.session.completed".equals(event.getType())) {
                log.info("Ignoring Stripe event type={}", event.getType());
                return;
            }

            Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();

            String orderIdStr = session.getMetadata() != null ? session.getMetadata().get("orderId") : null;
            log.info("Stripe session metadata={}", session.getMetadata());

            if (orderIdStr == null || orderIdStr.isBlank()) {
                log.warn("orderId metadata missing in checkout.session.completed. eventId={}", event.getId());
                return;
            }

            Long orderId = Long.valueOf(orderIdStr);
            Order o = orderRepository.findById(orderId).orElse(null);

            if (o == null) {
                log.warn("Order not found for webhook. orderId={}", orderId);
                return;
            }

            o.setStatus("COMPLETED");
            o.setCompletedAt(DateUtil.now());
            orderRepository.save(o);
            log.info("Order marked COMPLETED. orderId={}", o.getId());

            try {
                pdfReceiptService.generateForOrder(o);
                log.info("Receipt generated. orderId={}", o.getId());
            } catch (Exception receiptEx) {
                log.error("Receipt generation failed for orderId={}", o.getId(), receiptEx);
            }

        } catch (Exception e) {
            log.error("Stripe webhook processing failed", e);
            throw new RuntimeException("Webhook error: " + e.getMessage(), e);
        }
    }
}