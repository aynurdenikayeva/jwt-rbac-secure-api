package com.aynur.payment.receipt.service;

import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.entity.Receipt;
import com.aynur.payment.domain.repository.ReceiptRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailReceiptService {
    private final JavaMailSender mailSender;
    private final ReceiptRepository receiptRepository;

    public void sendReceipt(Order order, String toEmail) {
        try {
            Receipt receipt = receiptRepository.findByOrderId(order.getId())
                    .orElseThrow(() -> new RuntimeException("Receipt not found"));
            File file = new File(receipt.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("Receipt file not found");
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Payment Receipt - Order #" + order.getId());
            helper.setText(buildEmailBody(order), true);
            helper.addAttachment(file.getName(), new FileSystemResource(file));
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
    private String buildEmailBody(Order order) {
        return """
                <h2>Payment Receipt</h2>
                <p>Order ID: %d</p>
                <p>Amount: %d</p>
                <p>Currency: %s</p>
                <p>Status: %s</p>
                <br/>
                <p>Thank you for your payment.</p>
                """.formatted(
                order.getId(),
                order.getAmountMinor(),
                order.getCurrency(),
                order.getStatus()
        );
    }
}