package dev.vegui.eventdeck.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

public class Util {

    static public String truncateWithEllipsis(String str, int maxLength) {

        if (str.length() <= maxLength) {
            return str;
        }

        if (maxLength <= 3) {
            return str.substring(0, maxLength);
        }

        return str.substring(0, maxLength - 3) + "...";

    }

    public static void openFile(File file) {
        try {
            if (!Desktop.isDesktopSupported()) {
                throw new RuntimeException("Desktop no está soportado en este sistema");
            }

            if (!file.exists()) {
                throw new RuntimeException("El archivo no existe: " + file.getAbsolutePath());
            }

            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo abrir el archivo", e);
        }
    }

    public static String toDataPngBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            ImageIO.write(image, "png", output);

            byte[] imageBytes = output.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Could not convert image to base64 PNG", e);
        }
    }


}
