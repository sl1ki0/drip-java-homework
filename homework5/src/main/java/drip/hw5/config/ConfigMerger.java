package drip.hw5.config;

import drip.hw5.cli.CommandLineOptions;

public final class ConfigMerger {
    public RenderConfig merge(RenderConfig baseConfig, CommandLineOptions options) {
        return new RenderConfig(
            new ImageSize(
                options.width() != null ? options.width() : baseConfig.size().width(),
                options.height() != null ? options.height() : baseConfig.size().height()
            ),
            options.iterationCount() != null ? options.iterationCount() : baseConfig.iterationCount(),
            options.outputPath() != null ? options.outputPath() : baseConfig.outputPath(),
            options.threads() != null ? options.threads() : baseConfig.threads(),
            options.seed() != null ? options.seed() : baseConfig.seed(),
            options.functions() != null ? options.functions() : baseConfig.functions(),
            options.affineParams() != null ? options.affineParams() : baseConfig.affineParams(),
            options.symmetryLevel() != null ? options.symmetryLevel() : baseConfig.symmetryLevel()
        );
    }
}
