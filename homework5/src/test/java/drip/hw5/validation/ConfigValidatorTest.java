package drip.hw5.validation;

import drip.hw5.config.ImageSize;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.RenderConfigDefaults;
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
}
