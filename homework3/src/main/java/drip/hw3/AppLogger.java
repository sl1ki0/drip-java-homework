package drip.hw3;

public final class AppLogger {
    private AppLogger() {
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    private static void log(String level, String message) {
        System.out.println("[" + level + "] " + message);
    }
}
