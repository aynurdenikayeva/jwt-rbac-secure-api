package com.aynur.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // order sahibi
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long amountMinor; // 10.50 AZN yerinə 1050 kimi (Stripe best practice)

    @Column(nullable = false)
    private String currency; // "usd", "eur"

    private String description;

    private String stripeSessionId;
    private String stripeSessionUrl;

    @Column(nullable = false)
    private String status; // PENDING/COMPLETED/FAILED/CANCELED

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}