package drip.hw3;

import java.time.Instant;
import java.time.LocalDate;

public record LogEntry(
        Instant timestamp,
        LocalDate date,
        String resource,
        int responseCode,
        long responseSize
) {}
