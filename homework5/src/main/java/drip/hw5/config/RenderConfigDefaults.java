package drip.hw5.config;

import java.nio.file.Path;
import java.util.List;

public final class RenderConfigDefaults {
    private RenderConfigDefaults() {
    }

    public static RenderConfig defaultConfig() {
        return new RenderConfig(
            new ImageSize(1920, 1080),
            2500,
            Path.of("result.png"),
            1,
            5L,
            List.of(
                new VariationConfig(VariationType.LINEAR, 1.0),
                new VariationConfig(VariationType.SINUSOIDAL, 1.0),
                new VariationConfig(VariationType.SPHERICAL, 1.0),
                new VariationConfig(VariationType.SWIRL, 1.0),
                new VariationConfig(VariationType.HORSESHOE, 1.0)
            ),
            List.of(
                new AffineTransformConfig(0.45, 0.12, -0.35, -0.12, 0.45, 0.10),
                new AffineTransformConfig(0.40, -0.35, 0.40, 0.35, 0.40, 0.05),
                new AffineTransformConfig(0.38, 0.28, -0.10, -0.28, 0.38, -0.45)
            ),
            1
        );
    }
}
