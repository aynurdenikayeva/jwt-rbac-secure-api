package com.aynur.payment.payment.stripe;

import com.aynur.payment.common.util.DateUtil;
import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.entity.WebhookEventLog;
import com.aynur.payment.domain.repository.OrderRepository;
import com.aynur.payment.domain.repository.WebhookEventLogRepository;
import com.aynur.payment.receipt.service.PdfReceiptService;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final WebhookEventLogRepository eventLogRepository;
    private final OrderRepository orderRepository;
    private final PdfReceiptService pdfReceiptService;

    public void handle(String payload, String signatureHeader) {

        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);

            // idempotency: eyni event 2 dəfə gəlməsin
            if (eventLogRepository.findByStripeEventId(event.getId()).isPresent()) {
                return;
            }

            eventLogRepository.save(WebhookEventLog.builder()
                    .stripeEventId(event.getId())
                    .type(event.getType())
                    .payloadSummary(payload.length() > 1000 ? payload.substring(0, 1000) : payload)
                    .createdAt(DateUtil.now())
                    .build());

            // ---- checkout.session.completed ----
            if ("checkout.session.completed".equals(event.getType())) {

                StripeObject stripeObject = event.getDataObjectDeserializer()
                        .getObject()
                        .orElse(null);

                if (stripeObject instanceof Session session) {

                    String orderIdStr = session.getMetadata() != null
                            ? session.getMetadata().get("orderId")
                            : null;

                    if (orderIdStr != null) {
                        Long orderId = Long.valueOf(orderIdStr);

                        Order o = orderRepository.findById(orderId).orElse(null);
                        if (o != null) {
                            o.setStatus("COMPLETED");
                            o.setCompletedAt(DateUtil.now());
                            orderRepository.save(o);

                            // PDF receipt yarat
                            pdfReceiptService.generateForOrder(o);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Webhook error: " + e.getMessage());
        }
    }
}