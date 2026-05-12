package drip.hw5.render;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.ImageSize;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import drip.hw5.logging.AppLogger;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RenderPerformanceTest {
    @Test
    @Disabled("Manual performance check for 1, 2, 4 and 8 threads.")
    void printsExecutionTimes() {
        for (int threads : List.of(1, 2, 4, 8)) {
            RenderConfig config = new RenderConfig(
                new ImageSize(800, 600),
                20_000,
                Path.of("result.png"),
                threads,
                31L,
                List.of(
                    new VariationConfig(VariationType.LINEAR, 1.0),
                    new VariationConfig(VariationType.SWIRL, 1.0),
                    new VariationConfig(VariationType.HORSESHOE, 0.8)
                ),
                List.of(
                    new AffineTransformConfig(0.45, 0.12, -0.35, -0.12, 0.45, 0.10),
                    new AffineTransformConfig(0.40, -0.35, 0.40, 0.35, 0.40, 0.05),
                    new AffineTransformConfig(0.38, 0.28, -0.10, -0.28, 0.38, -0.45)
                ),
                3
            );

            long startedAt = System.nanoTime();
            new FractalFlameRenderer(new AppLogger()).render(config);
            long elapsedMs = (System.nanoTime() - startedAt) / 1_000_000L;
            System.out.println("threads=%d elapsedMs=%d".formatted(threads, elapsedMs));
        }
    }
}
