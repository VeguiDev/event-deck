package dev.vegui.eventdeck.util;

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
}
