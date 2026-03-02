package com.aynur.payment.qrcode.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    public byte[] generatePng(String text, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.QR_CODE, size, size, hints);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("QR generate failed: " + e.getMessage());
        }
    }
}