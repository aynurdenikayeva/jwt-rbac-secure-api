package com.aynur.payment.payment.stripe;

import com.aynur.payment.config.StripeConfig;
import com.aynur.payment.domain.entity.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutService {

    public record CheckoutSessionResult(String sessionId, String sessionUrl) {}

    public CheckoutSessionResult createCheckoutSession(Order order) {
        if (!StripeConfig.STRIPE_ENABLED) {
            throw new IllegalStateException("Stripe is disabled: missing/invalid stripe.api-key (set STRIPE_API_KEY env or stripe.api-key in yml)");
        }

        try {
            if (order == null) throw new IllegalArgumentException("Order is null");
            if (order.getAmountMinor() == null || order.getAmountMinor() <= 0)
                throw new IllegalArgumentException("amountMinor must be > 0");
            if (order.getCurrency() == null || order.getCurrency().isBlank())
                throw new IllegalArgumentException("currency is required");

            String currency = order.getCurrency().trim().toLowerCase();

            String name = (order.getDescription() == null || order.getDescription().isBlank())
                    ? "Order #" + order.getId()
                    : order.getDescription().trim();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://example.com/success?orderId=" + order.getId())
                    .setCancelUrl("https://example.com/cancel?orderId=" + order.getId())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)
                                                    .setUnitAmount(order.getAmountMinor())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(name)
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

        } catch (StripeException e) {
            throw new RuntimeException("Stripe session create failed: " + e.getMessage(), e);
        }
    }
}