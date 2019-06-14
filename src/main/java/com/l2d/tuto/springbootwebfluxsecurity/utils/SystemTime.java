package com.l2d.tuto.springbootwebfluxsecurity.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class SystemTime {

    public static final String TIME_ZONE_NAME = "UTC";

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(TIME_ZONE_NAME);

    public static final ZoneId TIME_ZONE_ID = ZoneId.of(TIME_ZONE_NAME);

    public static final ZoneOffset TIME_ZONE_OFFSET = ZoneOffset.UTC;

    private SystemTime() {
        throw new UnsupportedOperationException();
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(TIME_ZONE_ID).truncatedTo(ChronoUnit.MILLIS);
    }

    public static LocalDate today() {
        return LocalDate.now(TIME_ZONE_ID);
    }
}
