package dev.vegui.eventdeck.util;

import java.awt.*;
import java.io.File;

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

}
