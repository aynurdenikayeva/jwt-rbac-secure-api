package com.aynur.payment.payment.scheduler;

import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpiredSessionCleanupJob {

    private final OrderRepository orderRepository;

    // hər 1 dəqiqədə bir
    @Scheduled(fixedDelay = 60_000)
    public void cleanup() {

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Order> expiredOrders = orderRepository.findAll()
                .stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .filter(o -> o.getCreatedAt() != null && o.getCreatedAt().isBefore(threshold))
                .toList();

        for (Order o : expiredOrders) {
            o.setStatus("CANCELED");
            orderRepository.save(o);
        }
    }
}