package drip.hw3;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogEntryParserTest {
    private final LogEntryParser parser = new LogEntryParser();

    @Test
    void shouldParseValidLogLine() {
        String line = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] " +
                "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" " +
                "\"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

        Optional<LogEntry> parsed = parser.parse(line);

        assertTrue(parsed.isPresent());
        assertEquals("/downloads/product_1", parsed.get().resource());
        assertEquals(304, parsed.get().responseCode());
        assertEquals(0L, parsed.get().responseSize());
        assertEquals(LocalDate.of(2015, 5, 17), parsed.get().date());
    }

    @Test
    void shouldSkipInvalidLine() {
        assertTrue(parser.parse("broken line").isEmpty());
    }
}
