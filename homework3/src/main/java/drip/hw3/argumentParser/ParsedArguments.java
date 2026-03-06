package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.util.List;

public record ParsedArguments(
        List<Path> paths,
        String format,
        Path output,
        String from,
        String to
) {}
