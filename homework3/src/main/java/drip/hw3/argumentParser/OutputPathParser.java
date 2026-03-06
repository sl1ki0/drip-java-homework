package drip.hw3.argumentParser;

import java.nio.file.Files;
import java.nio.file.Path;

public final class OutputPathParser {

    private OutputPathParser() {}

    public static Path parse(String output, String format) {
        ParserUtils.requireNonBlank(output, "Путь к выходному файлу не может быть пустым");
        ParserUtils.requireNonBlank(format, "Формат вывода не указан");

        Path outputPath = Path.of(output).normalize();

        String expectedExtension = switch (ParserUtils.normalizeValue(format)) {
            case "markdown" -> "md";
            case "json" -> "json";
            default -> throw new IllegalArgumentException("Неподдерживаемый формат: " + format);
        };

        ParserUtils.validateExtension(outputPath, expectedExtension);
        validateFileDoesNotExist(outputPath);
        validateDirectory(outputPath);

        return outputPath;
    }

    private static void validateFileDoesNotExist(Path path) {

        if (Files.exists(path)) {
            throw new IllegalArgumentException(
                    "Файл уже существует: " + path
            );
        }
    }
    private static void validateDirectory(Path path) {

        Path parent = path.toAbsolutePath().getParent();

        if (parent == null) {
            throw new IllegalArgumentException(
                    "Не удалось определить директорию для выходного файла"
            );
        }

        if (!Files.exists(parent)) {
            throw new IllegalArgumentException(
                    "Указанная директория не существует: " + parent
            );
        }

        if (!Files.isDirectory(parent)) {
            throw new IllegalArgumentException(
                    "Указанный путь не является директорией: " + parent
            );
        }

        if (!Files.isWritable(parent)) {
            throw new IllegalArgumentException(
                    "Нет прав на запись в директорию: " + parent
            );
        }
    }
}
