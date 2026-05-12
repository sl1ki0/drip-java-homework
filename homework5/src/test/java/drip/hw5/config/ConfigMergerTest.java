package drip.hw5.config;

import drip.hw5.cli.CommandLineOptions;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigMergerTest {
    @Test
    void commandLineOverridesBaseConfig() {
        RenderConfig baseConfig = RenderConfigDefaults.defaultConfig();
        CommandLineOptions overrides = new CommandLineOptions(
            640,
            480,
            42L,
            1234,
            Path.of("custom.png"),
            8,
            List.of(new AffineTransformConfig(1, 0, 0, 0, 1, 0)),
            List.of(new VariationConfig(VariationType.LINEAR, 2.0)),
            null,
            5
        );

        RenderConfig merged = new ConfigMerger().merge(baseConfig, overrides);

        assertEquals(640, merged.size().width());
        assertEquals(480, merged.size().height());
        assertEquals(42L, merged.seed());
        assertEquals(1234, merged.iterationCount());
        assertEquals("custom.png", merged.outputPath().toString());
        assertEquals(8, merged.threads());
        assertEquals(1, merged.affineParams().size());
        assertEquals(2.0, merged.functions().getFirst().weight());
        assertEquals(5, merged.symmetryLevel());
    }
}
