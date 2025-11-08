package dev.huntbot.util.time;

import lombok.Getter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

public final class TimeUtil {
    @Getter
    private final static DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
        .appendPattern("MMMM d, yyyy")
        .toFormatter();

    @Getter
    private final static DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("MMMM d, yyyy h:mm:ss a")
        .toFormatter();

    public static long getCurMilli() {
        return Instant.now().toEpochMilli();
    }

    public static long getCurSec() {
        return getCurMilli() / 1000;
    }

    public static long getOneDaySec() {
        return 60 * 60 * 24;
    }

    public static long getOneDayMilli() {
        return getOneDaySec() * 1000;
    }

    public static long getThirtyMinMilli() {
        return 1000 * 60 * 30;
    }

    public static long getPriorMinuteSecs(long timestamp) {
        return ((timestamp / 60) - 1) * 60;
    }

    public static long getNextBootsSec() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.ofHours(-5));
        int daysUntilTuesday = (DayOfWeek.TUESDAY.getValue() - now.getDayOfWeek().getValue() + 7) % 7;
        if (daysUntilTuesday == 0) daysUntilTuesday = 7;

        ZonedDateTime nextTuesday = now.plusDays(daysUntilTuesday).toLocalDate().atStartOfDay(ZoneOffset.ofHours(-5));

        return nextTuesday.toEpochSecond();
    }

    public static long getLastBootsSec() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.ofHours(-5));
        int daysSinceTuesday = (now.getDayOfWeek().getValue() - DayOfWeek.TUESDAY.getValue() + 7) % 7;
        if (daysSinceTuesday == 0) daysSinceTuesday = 7;

        ZonedDateTime lastTuesday = now.minusDays(daysSinceTuesday).toLocalDate().atStartOfDay(ZoneOffset.ofHours(-5));

        return lastTuesday.toEpochSecond();
    }

    public static int getDayOfYear() {
        LocalDate now = LocalDate.now(ZoneOffset.ofHours(-5));
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
        return (int) ChronoUnit.DAYS.between(startOfYear, now) + 1;
    }
}
