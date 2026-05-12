package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ArgumentParser {
    private ArgumentParser() {}

    public static ParsedArguments parseArguments(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Аргументы не переданы");
        }

        List<String> pathArguments = new ArrayList<>();
        String format = null;
        String output = null;
        String from = null;
        String to = null;

        for (int index = 0; index < args.length; index++) {
            String current = args[index];

            switch (current) {
                case "--path", "-p" -> {
                    index++;
                    while (index < args.length && !ParserUtils.isOption(args[index])) {
                        pathArguments.add(args[index]);
                        index++;
                    }
                    index--;
                }
                case "--format", "-f" ->
                    format = ParserUtils.readSingleArgument(args, ++index, current);

                case "--output", "-o" ->
                    output = ParserUtils.readSingleArgument(args, ++index, current);

                case "--from" ->
                    from = ParserUtils.readSingleArgument(args, ++index, current);

                case "--to" ->
                    to = ParserUtils.readSingleArgument(args, ++index, current);

                default -> throw new IllegalArgumentException("Неизвестный аргумент: " + current);
            }
        }

        if (pathArguments.isEmpty()) {
            throw new IllegalArgumentException("Нужно указать хотя бы один путь через --path или -p");
        }

        ParserUtils.requireNonBlank(format, "Параметр --format обязателен");
        ParserUtils.requireNonBlank(output, "Параметр --output обязателен");

        List<Path> paths = new ArrayList<>();
        for (String pathArgument : pathArguments) {
            paths.addAll(InputPathParser.resolvePath(pathArgument));
        }

        String normalizedFormat = ParserUtils.normalizeFormat(format);
        Path outputPath = OutputPathParser.parse(output, normalizedFormat);
        ParserUtils.validateDateRange(from, to);

        return new ParsedArguments(paths, normalizedFormat, outputPath, from, to);
    }
}
