package com.aynur.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api-key:}")
    private String apiKeyFromProps;

    public static volatile boolean STRIPE_ENABLED = false;

    @PostConstruct
    public void init() {
        String envKey = System.getenv("STRIPE_API_KEY");

        String resolved = (envKey != null && !envKey.isBlank())
                ? envKey.trim()
                : (apiKeyFromProps == null ? "" : apiKeyFromProps.trim());

        if (resolved.isBlank()) {
            STRIPE_ENABLED = false;
            System.out.println("Stripe is DISABLED: stripe.api-key is missing.");
            return;
        }

        if (!(resolved.startsWith("sk_test_") || resolved.startsWith("sk_live_"))) {
            STRIPE_ENABLED = false;
            System.out.println("Stripe is DISABLED: stripe.api-key format is invalid (must start with sk_test_ or sk_live_).");
            return;
        }

        Stripe.apiKey = resolved;
        STRIPE_ENABLED = true;

        String masked = resolved.length() <= 12
                ? "****"
                : resolved.substring(0, 7) + "****" + resolved.substring(resolved.length() - 4);

        System.out.println("Stripe is ENABLED. Key loaded: " + masked);
        System.out.println("Stripe key check => prefix=" + resolved.substring(0, 8)
                + " len=" + resolved.length()
                + " last4=" + resolved.substring(resolved.length() - 4));
    }
}