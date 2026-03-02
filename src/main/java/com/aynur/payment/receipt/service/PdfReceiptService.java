package com.aynur.payment.receipt.service;

import com.aynur.payment.domain.entity.Order;
import com.aynur.payment.domain.entity.Receipt;
import com.aynur.payment.domain.repository.ReceiptRepository;
import com.aynur.payment.common.util.DateUtil;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
public class PdfReceiptService {

    private final ReceiptRepository receiptRepository;

    public Receipt generateForOrder(Order order) {
        try {
            Path dir = Paths.get("data", "receipts");
            Files.createDirectories(dir);

            String fileName = "receipt-order-" + order.getId() + ".pdf";
            Path file = dir.resolve(fileName);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file.toFile()));
            document.open();

            document.add(new Paragraph("Payment Receipt"));
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("Amount (minor): " + order.getAmountMinor()));
            document.add(new Paragraph("Currency: " + order.getCurrency()));
            document.add(new Paragraph("Status: " + order.getStatus()));
            document.add(new Paragraph("Created: " + order.getCreatedAt()));
            document.add(new Paragraph("Completed: " + order.getCompletedAt()));

            document.close();

            Receipt receipt = receiptRepository.findByOrderId(order.getId())
                    .orElse(Receipt.builder().orderId(order.getId()).build());

            receipt.setFilePath(file.toString());
            receipt.setGeneratedAt(DateUtil.now());
            return receiptRepository.save(receipt);

        } catch (Exception e) {
            throw new RuntimeException("PDF generate failed: " + e.getMessage());
        }
    }
}