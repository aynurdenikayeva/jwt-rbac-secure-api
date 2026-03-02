package com.aynur.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_event_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WebhookEventLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String stripeEventId;

    private String type;

    @Column(length = 4000)
    private String payloadSummary;

    private LocalDateTime createdAt;
}