package com.aynur.payment.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Receipt {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private String filePath;

    private LocalDateTime generatedAt;
}