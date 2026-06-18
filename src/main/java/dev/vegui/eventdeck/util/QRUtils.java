package dev.vegui.eventdeck.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QRUtils {

    public static BufferedImage generateQR(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size,
                    hints
            );

            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new RuntimeException("No se pudo generar el QR", e);
        }
    }
}