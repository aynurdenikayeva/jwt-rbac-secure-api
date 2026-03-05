package com.aynur.payment.payment.controller;

import com.aynur.payment.common.response.ApiResponse;
import com.stripe.model.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stripe")
public class StripeAdminController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() throws Exception {
        Account account = Account.retrieve();
        return ApiResponse.ok("OK: " + account.getId());
    }
}
