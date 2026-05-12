package drip.hw5.render;

import drip.hw5.config.RenderConfig;
import drip.hw5.config.RenderConfigDefaults;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import drip.hw5.logging.AppLogger;
import java.util.List;
import java.util.random.RandomGeneratorFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RenderSupportTest {
    @Test
    void weightedSelectorRespectsZeroWeightEntries() {
        WeightedSelector<String> selector = new WeightedSelector<>(
            List.of("skip", "take"),
            List.of(0.0, 1.0)
        );

        String selected = selector.select(RandomGeneratorFactory.of("L64X128MixRandom").create(7L));
        assertEquals("take", selected);
    }

    @Test
    void imageBufferMergesAndCreatesImage() {
        FlameImageBuffer left = new FlameImageBuffer(10, 10);
        FlameImageBuffer right = new FlameImageBuffer(10, 10);
        left.addPoint(2, 3, new RgbColor(1.0, 0.0, 0.0));
        right.addPoint(4, 5, new RgbColor(0.0, 1.0, 0.0));

        left.merge(right);

        assertTrue(left.hasHits());
        assertEquals(2L, left.totalHits());
        assertEquals(10, left.toBufferedImage().getWidth());
        assertEquals(10, left.toBufferedImage().getHeight());
    }

    @Test
    void symmetryLevelDuplicatesMorePoints() {
        RenderConfig base = RenderConfigDefaults.defaultConfig();
        RenderConfig symmetryOne = new RenderConfig(
            base.size(),
            200,
            base.outputPath(),
            1,
            11L,
            List.of(new VariationConfig(VariationType.LINEAR, 1.0)),
            base.affineParams(),
            1
        );
        RenderConfig symmetryFour = new RenderConfig(
            base.size(),
            200,
            base.outputPath(),
            1,
            11L,
            List.of(new VariationConfig(VariationType.LINEAR, 1.0)),
            base.affineParams(),
            4
        );

        FractalFlameRenderer renderer = new FractalFlameRenderer(new AppLogger());
        FlameImageBuffer single = renderer.render(symmetryOne);
        FlameImageBuffer symmetric = renderer.render(symmetryFour);

        assertTrue(symmetric.totalHits() > single.totalHits());
    }
}
