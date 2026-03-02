package com.aynur.payment.payment.controller;

import com.aynur.payment.common.response.ApiResponse;
import com.aynur.payment.payment.stripe.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    @PostMapping("/stripe")
    public ApiResponse<Object> stripeWebhook(@RequestBody String payload,
                                             @RequestHeader("Stripe-Signature") String signature) {
        webhookService.handle(payload, signature);
        return ApiResponse.ok(null);
    }
}