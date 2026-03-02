package com.aynur.payment.payment.stripe;

import com.aynur.payment.domain.entity.Order;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutService {

    public record CheckoutSessionResult(String sessionId, String sessionUrl) {}

    public CheckoutSessionResult createCheckoutSession(Order order) {
        try {
            // Stripe amount “minor” (cents) istəyir
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success?orderId=" + order.getId())
                    .setCancelUrl("https://example.com/cancel?orderId=" + order.getId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(order.getCurrency())
                                                    .setUnitAmount(order.getAmountMinor())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(order.getDescription() == null ? "Order #" + order.getId() : order.getDescription())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("orderId", String.valueOf(order.getId()))
                    .build();

            Session session = Session.create(params);
            return new CheckoutSessionResult(session.getId(), session.getUrl());

        } catch (Exception e) {
            throw new RuntimeException("Stripe session create failed: " + e.getMessage());
        }
    }
}