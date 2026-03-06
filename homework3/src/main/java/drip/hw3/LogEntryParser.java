package drip.hw3;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogEntryParser {
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) - (\\S+) \\[([^]]+)] \"([^\"]*)\" (\\d{3}) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\"$"
    );

    private static final DateTimeFormatter LOG_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public Optional<LogEntry> parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return Optional.empty();
        }

        try {
            String request = matcher.group(4);
            String[] requestParts = request.split(" ");
            if (requestParts.length < 2) {
                return Optional.empty();
            }

            OffsetDateTime dateTime = OffsetDateTime.parse(matcher.group(3), LOG_DATE_FORMATTER);
            Instant timestamp = dateTime.toInstant();
            LocalDate date = dateTime.toLocalDate();
            String resource = requestParts[1];
            int responseCode = Integer.parseInt(matcher.group(5));
            long responseSize = parseResponseSize(matcher.group(6));

            return Optional.of(new LogEntry(timestamp, date, resource, responseCode, responseSize));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private long parseResponseSize(String value) {
        return "-".equals(value) ? 0L : Long.parseLong(value);
    }
}
