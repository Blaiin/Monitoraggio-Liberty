package it.dmi.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

public class TimeUtils {

    /**
     * Calculates the duration in seconds between two temporal points.
     *
     * @param inizio (inclusive) the start temporal point
     * @param fine (exclusive) the end temporal point
     * @return the duration in seconds between the start and end temporal points
     */
    public static Long duration(Temporal inizio, Temporal fine) {
        return Duration.between(inizio, fine).getSeconds();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

}
