package dev.vegui.eventdeck.util;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class TicketPdfGenerator {

    public static void generate(String path, String eventTitle, String attendeeName, String code) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            document.add(new Paragraph("Entrada EventDeck"));
            document.add(new Paragraph("Evento: " + eventTitle));
            document.add(new Paragraph("Asistente: " + attendeeName));
            document.add(new Paragraph("Codigo: " + code));
            document.add(new Paragraph(" "));

            BufferedImage qrImage = QRUtils.generateQR("ticket:" + code, 250);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);

            Image pdfQrImage = Image.getInstance(baos.toByteArray());
            pdfQrImage.scaleToFit(250, 250);

            document.add(pdfQrImage);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF de la entrada", e);
        }
    }
}