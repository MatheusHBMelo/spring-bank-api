package br.com.springbank.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatetimeFormatter {
    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
}
