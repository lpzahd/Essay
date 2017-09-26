package com.lpzahd.essay.tool;

import com.lpzahd.base.NoInstance;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ)
 */
public class DateTime extends NoInstance {

    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String format(long milli) {
        return format(Instant.ofEpochMilli(milli), DEFAULT_DATE_TIME_PATTERN);
    }

    public static String format(Instant instant) {
        return format(instant, DEFAULT_DATE_TIME_PATTERN);
    }

    public static String format(long milli, String pattern) {
        return format(Instant.ofEpochMilli(milli), pattern);
    }

    public static String format(Instant instant, String pattern) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    public static long now() {
        return Instant.now().toEpochMilli();
    }

}
