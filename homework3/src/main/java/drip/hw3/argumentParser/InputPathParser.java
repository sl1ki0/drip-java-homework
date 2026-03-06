package drip.hw3.argumentParser;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class InputPathParser {
    private static final String[] SUPPORTED_LOG_EXTENSIONS = {"log", "txt"};

    private InputPathParser() {
    }

    public static List<Path> resolvePath(String pathArg) {
        ParserUtils.requireNonBlank(pathArg, "Путь к файлу не указан");

        List<Path> result = new ArrayList<>();

        if (hasWildcards(pathArg)) {
            result.addAll(resolveWildcard(pathArg));
        } else {
            Path path = Paths.get(pathArg);

            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Файл не найден: " + pathArg);
            }

            if (!Files.isRegularFile(path)) {
                throw new IllegalArgumentException("Это не файл: " + pathArg);
            }

            validateLogExtension(path);
            result.add(path.toAbsolutePath().normalize());
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("По указанному пути ничего не найдено: " + pathArg);
        }

        return result;
    }

    private static boolean hasWildcards(String path) {
        return path.contains("*") || path.contains("?") || path.contains("[");
    }

    private static List<Path> resolveWildcard(String wildcard) {
        List<Path> result = new ArrayList<>();

        Path startDir = extractStartDirectory(wildcard);
        if (!Files.exists(startDir) || !Files.isDirectory(startDir)) {
            throw new IllegalArgumentException("Директория не найдена: " + startDir);
        }

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);

        try (Stream<Path> paths = Files.walk(startDir)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .forEach(path -> {
                        validateLogExtension(path);
                        result.add(path.toAbsolutePath().normalize());
                    });
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка при чтении файлов: " + e.getMessage(), e);
        }

        return result;
    }

    private static Path extractStartDirectory(String wildcard) {
        int firstWildcard = firstWildcardIndex(wildcard);

        Path pathRoot = Paths.get(".");
        if (firstWildcard == -1) {
            Path path = Paths.get(wildcard).getParent();
            return path == null ? pathRoot : path;
        }

        String prefix = wildcard.substring(0, firstWildcard);
        Path prefixPath = Paths.get(prefix.isEmpty() ? "." : prefix);
        Path parent = Files.isDirectory(prefixPath) ? prefixPath : prefixPath.getParent();

        return parent == null ? pathRoot : parent;
    }

    private static int firstWildcardIndex(String s) {
        int star = s.indexOf('*');
        int question = s.indexOf('?');
        int bracket = s.indexOf('[');

        int min = Integer.MAX_VALUE;

        if (star != -1) min = star;
        if (question != -1) min = Math.min(min, question);
        if (bracket != -1) min = Math.min(min, bracket);

        return min == Integer.MAX_VALUE ? -1 : min;
    }

    private static void validateLogExtension(Path path) {
        try {
            ParserUtils.validateExtension(path, SUPPORTED_LOG_EXTENSIONS);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Неподдерживаемый формат файла: " + path + ". Разрешены только .log и .txt"
            );
        }
    }
}
