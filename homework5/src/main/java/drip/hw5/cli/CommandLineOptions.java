package drip.hw5.cli;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.VariationConfig;
import java.nio.file.Path;
import java.util.List;

public record CommandLineOptions(
    Integer width,
    Integer height,
    Long seed,
    Integer iterationCount,
    Path outputPath,
    Integer threads,
    List<AffineTransformConfig> affineParams,
    List<VariationConfig> functions,
    Path configPath,
    Integer symmetryLevel
) {
}
