package com.abc.library.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Tiện ích chuyển đổi và kiểm tra định dạng ngày tháng chuẩn dd/MM/yyyy.
 */
public final class DateUtil {
    public static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DateUtil() {
    }

    /**
     * Chuyển chuỗi dd/MM/yyyy sang LocalDate. Ném IllegalArgumentException nếu sai định dạng hoặc ngày không hợp lệ.
     */
    public static LocalDate parseDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }
        try {
            return LocalDate.parse(dateStr.trim(), DISPLAY_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày không hợp lệ. Vui lòng nhập đúng định dạng dd/MM/yyyy.", e);
        }
    }

    /**
     * Định dạng LocalDate thành chuỗi dd/MM/yyyy.
     */
    public static String formatDisplayDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_FORMATTER);
    }

    /**
     * Chuyển LocalDate sang java.sql.Date để lưu vào JDBC.
     */
    public static Date toSqlDate(LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }
}

