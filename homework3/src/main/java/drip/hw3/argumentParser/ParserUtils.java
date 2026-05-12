package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

public final class ParserUtils {
    private ParserUtils() {}

    public static boolean isOption(String value) {
        return value != null && value.startsWith("-");
    }

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public static String readSingleArgument(String[] args, int index, String optionName) {
        if (index >= args.length || isOption(args[index])) {
            throw new IllegalArgumentException("Для аргумента " + optionName + " не указано значение");
        }

        return args[index];
    }

    public static String normalizeFormat(String format) {
        String normalized = requireNonBlank(format, "Формат вывода не указан").toLowerCase(Locale.ROOT);

        if (!normalized.equals("json") && !normalized.equals("markdown")) {
            throw new IllegalArgumentException("Неподдерживаемый формат вывода: " + format);
        }

        return normalized;
    }

    public static String normalizeValue(String value) {
        return requireNonBlank(value, "Значение не может быть пустым").toLowerCase(Locale.ROOT);
    }

    public static void validateExtension(Path path, String... allowedExtensions) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            throw new IllegalArgumentException("Файл должен иметь одно из расширений: " + formatExtensions(allowedExtensions));
        }

        String actualExtension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        boolean isAllowed = Arrays.stream(allowedExtensions)
                .map(ParserUtils::normalizeValue)
                .anyMatch(actualExtension::equals);

        if (!isAllowed) {
            throw new IllegalArgumentException("Неверное расширение файла. Ожидается одно из: " + formatExtensions(allowedExtensions));
        }
    }

    public static void validateDateRange(String from, String to) {
        Instant fromInstant = parseFromBoundary(from);
        Instant toInstant = parseToBoundary(to);

        if (fromInstant != null && toInstant != null && !fromInstant.isBefore(toInstant)) {
            throw new IllegalArgumentException("Параметр --from должен быть меньше --to");
        }
    }

    public static Instant parseFromBoundary(String value) {
        ParsedDate parsedDate = parseDateArgument(value, "--from");
        return parsedDate == null ? null : parsedDate.fromInclusive();
    }

    public static Instant parseToBoundary(String value) {
        ParsedDate parsedDate = parseDateArgument(value, "--to");
        return parsedDate == null ? null : parsedDate.toInclusive();
    }

    private static ParsedDate parseDateArgument(String value, String argumentName) {
        if (value == null) {
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(value);
            return new ParsedDate(
                    date.atStartOfDay().toInstant(ZoneOffset.UTC),
                    date.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC)
            );
        } catch (DateTimeParseException ignored) {
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(value);
            Instant instant = dateTime.toInstant(ZoneOffset.UTC);
            return new ParsedDate(instant, instant);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Некорректное значение " + argumentName + ". Ожидается дата в формате ISO8601");
        }
    }

    private static String formatExtensions(String... extensions) {
        return Arrays.stream(extensions)
                .map(ParserUtils::normalizeValue)
                .map(extension -> "." + extension)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }

    private record ParsedDate(Instant fromInclusive, Instant toInclusive) {}
}
