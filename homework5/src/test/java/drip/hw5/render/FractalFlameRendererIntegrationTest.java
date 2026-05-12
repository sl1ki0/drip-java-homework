package drip.hw5.render;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.ImageSize;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import drip.hw5.logging.AppLogger;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FractalFlameRendererIntegrationTest {
    @Test
    void rendersImageInSingleAndMultiThreadModes() {
        RenderConfig singleThreadConfig = smallConfig(1);
        RenderConfig multiThreadConfig = smallConfig(4);

        FlameImageBuffer single = new FractalFlameRenderer(new AppLogger()).render(singleThreadConfig);
        FlameImageBuffer multi = new FractalFlameRenderer(new AppLogger()).render(multiThreadConfig);

        assertTrue(single.hasHits());
        assertTrue(multi.hasHits());
        assertEquals(120, single.toBufferedImage().getWidth());
        assertEquals(80, single.toBufferedImage().getHeight());
        assertEquals(120, multi.toBufferedImage().getWidth());
        assertEquals(80, multi.toBufferedImage().getHeight());
    }

    private RenderConfig smallConfig(int threads) {
        return new RenderConfig(
            new ImageSize(120, 80),
            500,
            Path.of("result.png"),
            threads,
            17L,
            List.of(
                new VariationConfig(VariationType.SWIRL, 1.0),
                new VariationConfig(VariationType.HORSESHOE, 0.5)
            ),
            List.of(
                new AffineTransformConfig(0.45, 0.12, -0.35, -0.12, 0.45, 0.10),
                new AffineTransformConfig(0.40, -0.35, 0.40, 0.35, 0.40, 0.05)
            ),
            2
        );
    }
}
