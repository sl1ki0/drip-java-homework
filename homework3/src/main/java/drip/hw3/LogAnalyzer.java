package drip.hw3;

import drip.hw3.argumentParser.ParsedArguments;
import drip.hw3.argumentParser.ParserUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class LogAnalyzer {
    private final LogEntryParser logEntryParser = new LogEntryParser();

    public LogAnalysisResult analyze(ParsedArguments arguments) throws IOException {
        Instant fromBoundary = ParserUtils.parseFromBoundary(arguments.from());
        Instant toBoundary = ParserUtils.parseToBoundary(arguments.to());

        StatisticsAccumulator accumulator = new StatisticsAccumulator();

        for (Path path : arguments.paths()) {
            AppLogger.info("Чтение файла " + path);
            processFile(path, fromBoundary, toBoundary, accumulator);
        }

        List<String> files = arguments.paths()
                .stream()
                .map(Path::toString)
                .toList();

        return accumulator.toResult(files);
    }

    private void processFile(
            Path path,
            Instant fromBoundary,
            Instant toBoundary,
            StatisticsAccumulator accumulator
    ) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                Optional<LogEntry> parsedEntry = logEntryParser.parse(line);
                if (parsedEntry.isEmpty()) {
                    AppLogger.warn("Пропущена некорректная строка в " + path + ":" + lineNumber);
                    continue;
                }

                LogEntry entry = parsedEntry.get();
                if (!isWithinRange(entry.timestamp(), fromBoundary, toBoundary)) {
                    continue;
                }

                accumulator.add(entry);
            }
        }
    }

    private boolean isWithinRange(Instant timestamp, Instant fromBoundary, Instant toBoundary) {
        if (fromBoundary != null && timestamp.isBefore(fromBoundary)) {
            return false;
        }

        return toBoundary == null || !timestamp.isAfter(toBoundary);
    }
}
