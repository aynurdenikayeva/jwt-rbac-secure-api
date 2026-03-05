package com.aynur.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

@SpringBootApplication(
        exclude = {
                RedisRepositoriesAutoConfiguration.class
        }
)
public class PaymentRbacApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentRbacApiApplication.class, args);
    }
}