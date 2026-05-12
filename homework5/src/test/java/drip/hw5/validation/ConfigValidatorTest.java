package drip.hw5.validation;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.ImageSize;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.RenderConfigDefaults;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigValidatorTest {
    private final ConfigValidator validator = new ConfigValidator();

    @Test
    void acceptsDefaultConfig() {
        assertDoesNotThrow(() -> validator.validate(RenderConfigDefaults.defaultConfig()));
    }

    @Test
    void rejectsInvalidValues() {
        RenderConfig base = RenderConfigDefaults.defaultConfig();
        RenderConfig invalidConfig = new RenderConfig(
            new ImageSize(-1, base.size().height()),
            0,
            base.outputPath(),
            0,
            base.seed(),
            base.functions(),
            base.affineParams(),
            0
        );

        assertThrows(ConfigValidationException.class, () -> validator.validate(invalidConfig));
    }

    @Test
    void rejectsEmptyFunctions() {
        RenderConfig base = RenderConfigDefaults.defaultConfig();
        RenderConfig invalidConfig = new RenderConfig(
            base.size(),
            base.iterationCount(),
            base.outputPath(),
            base.threads(),
            base.seed(),
            List.of(),
            base.affineParams(),
            base.symmetryLevel()
        );

        assertThrows(ConfigValidationException.class, () -> validator.validate(invalidConfig));
    }

    @Test
    void rejectsNonFiniteAffineCoefficients() {
        RenderConfig base = RenderConfigDefaults.defaultConfig();
        RenderConfig invalidConfig = new RenderConfig(
            new ImageSize(base.size().width(), base.size().height()),
            base.iterationCount(),
            base.outputPath(),
            base.threads(),
            base.seed(),
            List.of(new VariationConfig(VariationType.LINEAR, 1.0)),
            List.of(new AffineTransformConfig(Double.NaN, 0, 0, 0, 1, 0)),
            base.symmetryLevel()
        );

        assertThrows(ConfigValidationException.class, () -> validator.validate(invalidConfig));
    }
}
