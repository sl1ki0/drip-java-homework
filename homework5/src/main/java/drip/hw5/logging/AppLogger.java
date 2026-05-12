package drip.hw5.logging;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class AppLogger {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    private void log(String level, String message) {
        System.out.println("[%s] [%s] %s".formatted(LocalTime.now().format(TIME_FORMAT), level, message));
    }
}
