package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.time.Instant;
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

    public static IllegalArgumentException invalidArgument(String message) {
        return new IllegalArgumentException(message);
    }

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw invalidArgument(message);
        }

        return value;
    }

    public static String readSingleArgument(String[] args, int index, String optionName) {
        if (index >= args.length || isOption(args[index])) {
            throw invalidArgument("Для аргумента " + optionName + " не указано значение");
        }

        return args[index];
    }

    public static String normalizeFormat(String format) {
        String normalized = requireNonBlank(format, "Формат вывода не указан").toLowerCase(Locale.ROOT);

        if (!normalized.equals("json") && !normalized.equals("markdown")) {
            throw invalidArgument("Неподдерживаемый формат вывода: " + format);
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
            throw invalidArgument("Файл должен иметь одно из расширений: " + formatExtensions(allowedExtensions));
        }

        String actualExtension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        boolean isAllowed = Arrays.stream(allowedExtensions)
                .map(ParserUtils::normalizeValue)
                .anyMatch(actualExtension::equals);

        if (!isAllowed) {
            throw invalidArgument("Неверное расширение файла. Ожидается одно из: " + formatExtensions(allowedExtensions));
        }
    }

    public static void validateDateRange(String from, String to) {
        Instant fromInstant = parseDateArgument(from, "--from");
        Instant toInstant = parseDateArgument(to, "--to");

        if (fromInstant != null && toInstant != null && !fromInstant.isBefore(toInstant)) {
            throw invalidArgument("Параметр --from должен быть меньше --to");
        }
    }

    private static Instant parseDateArgument(String value, String argumentName) {
        if (value == null) {
            return null;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(value);
            return dateTime.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw invalidArgument("Некорректное значение " + argumentName + ". Ожидается LocalDateTime");
        }
    }


    private static String formatExtensions(String... extensions) {
        return Arrays.stream(extensions)
                .map(ParserUtils::normalizeValue)
                .map(extension -> "." + extension)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
