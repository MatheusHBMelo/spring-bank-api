package br.com.springbank.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DatetimeFormatterTest {
    @Test
    void testFormatDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 11, 15, 30, 45);

        String formattedDateTime = DatetimeFormatter.formatDateTime(dateTime);

        String expected = "11/06/2025 15:30:45";
        assertEquals(expected, formattedDateTime);
    }
}