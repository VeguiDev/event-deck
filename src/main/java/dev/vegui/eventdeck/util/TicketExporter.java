package dev.vegui.eventdeck.util;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import dev.vegui.eventdeck.model.Event;
import dev.vegui.eventdeck.model.Ticket;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketExporter {

    public static void exportToPDF(String path, Event event, Ticket ticket) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            document.add(new Paragraph("Entrada EventDeck"));
            document.add(new Paragraph("Evento: " + event.getTitle()));
            document.add(new Paragraph("Asistente: " + ticket.getAttendeeName()));
            document.add(new Paragraph("Dirección: " + event.getLocation().getFormattedAddress()));
            document.add(new Paragraph("Fecha: " + event.getStartDate()));
            document.add(new Paragraph(" "));

            BufferedImage qrImage = QRUtils.generateQR("ticket:" + ticket.getCode(), 250);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);

            Image pdfQrImage = Image.getInstance(baos.toByteArray());
            pdfQrImage.scaleToFit(250, 250);

            document.add(pdfQrImage);
            document.add(new Paragraph("Codigo: " + ticket.getCode()));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF de la entrada", e);
        }
    }

    public static String exportToString(Ticket ticket, Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("Entrada EventDeck\n\n");
        sb.append("Evento: " + event.getTitle());
        sb.append("\n");
        sb.append("Asistente: " + ticket.getAttendeeName());
        sb.append("\n");
        sb.append("Dirección: " + event.getLocation().getFormattedAddress());
        sb.append("\n");
        sb.append("Fecha: " + event.getStartDate().toString());
        sb.append("\n");
        sb.append("Código: " + ticket.getCode());

        return sb.toString();
    }

    public static String exportToHTML(Ticket ticket, Event event) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h2><b>Entrada EventDeck</b></h2>");

        sb.append("<img src=\"cid:ticket-qr\" width=\"250\" />");

        sb.append("<p><b>Evento:</b> " + event.getTitle() + "</p>");
        sb.append("<p><b>Asistente:</b> " + ticket.getAttendeeName() + "</p>");
        sb.append("<p><b>Dirección:</b> " + event.getLocation().getFormattedAddress() + "</p>");
        sb.append("<p><b>Fecha:</b> " + event.getStartDate().toString() + "</p>");
        sb.append("<p><b>Código:</b> " + ticket.getCode() + "</p>");

        sb.append("</html>");

        return sb.toString();
    }
}
