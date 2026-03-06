package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.util.List;

public record ParsedArguments(
        List<Path> paths,
        String format,
        String output,
        String from,
        String to
) {}