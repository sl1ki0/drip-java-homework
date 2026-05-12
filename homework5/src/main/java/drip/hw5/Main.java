package drip.hw5;

import drip.hw5.cli.CliArgumentParser;
import drip.hw5.cli.CommandLineException;
import drip.hw5.cli.CommandLineOptions;
import drip.hw5.config.ConfigMerger;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.RenderConfigDefaults;
import drip.hw5.image.PngImageWriter;
import drip.hw5.json.JsonConfigException;
import drip.hw5.json.JsonConfigLoader;
import drip.hw5.logging.AppLogger;
import drip.hw5.render.FlameImageBuffer;
import drip.hw5.render.FractalFlameRenderer;
import drip.hw5.validation.ConfigValidationException;
import drip.hw5.validation.ConfigValidator;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        AppLogger logger = new AppLogger();
        try {
            CommandLineOptions options = new CliArgumentParser().parse(args);
            RenderConfig fileConfig = options.configPath() == null
                ? RenderConfigDefaults.defaultConfig()
                : new JsonConfigLoader().load(options.configPath());

            RenderConfig config = new ConfigMerger().merge(fileConfig, options);
            new ConfigValidator().validate(config);

            logger.info("Starting render.");
            logger.info("Width=%d Height=%d Iterations=%d Threads=%d Symmetry=%d Output=%s"
                .formatted(
                    config.size().width(),
                    config.size().height(),
                    config.iterationCount(),
                    config.threads(),
                    config.symmetryLevel(),
                    config.outputPath()
                ));

            FlameImageBuffer imageBuffer = new FractalFlameRenderer(logger).render(config);
            new PngImageWriter().write(imageBuffer.toBufferedImage(), config.outputPath());
            logger.info("Image saved to %s".formatted(config.outputPath()));
        } catch (CommandLineException | JsonConfigException | ConfigValidationException exception) {
            System.err.println(exception.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error in Main: " + exception.getMessage());
            exception.printStackTrace(System.err);
        }
    }
}
