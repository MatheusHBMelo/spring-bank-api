package br.com.springbank.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatetimeFormatter {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
