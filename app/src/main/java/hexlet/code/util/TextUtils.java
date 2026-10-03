package hexlet.code.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public final class TextUtils {

    public static final int MAX_LENGTH = 200;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private TextUtils() {}

    public static String formatDate(Timestamp timestamp) {
        return timestamp == null ? "" : timestamp.toLocalDateTime().format(DATE_FORMAT);
    }

    /** Обрезает текст до {@link #MAX_LENGTH} символов и добавляет «...», если он был длиннее. */
    public static String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_LENGTH ? text : text.substring(0, MAX_LENGTH) + "...";
    }
}
