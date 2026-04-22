package drip.hw5.config;

import java.nio.file.Path;
import java.util.List;

public record RenderConfig(
    ImageSize size,
    int iterationCount,
    Path outputPath,
    int threads,
    long seed,
    List<VariationConfig> functions,
    List<AffineTransformConfig> affineParams,
    int symmetryLevel
) {
}
