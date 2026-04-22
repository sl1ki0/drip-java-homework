package drip.hw5.render;

import drip.hw5.config.VariationType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VariationApplierTest {
    private static final double EPSILON = 1.0e-9;

    @Test
    void appliesLinearVariation() {
        Point2D result = VariationApplier.apply(VariationType.LINEAR, new Point2D(0.5, -0.25));
        assertClose(0.5, result.x());
        assertClose(-0.25, result.y());
    }

    @Test
    void appliesSinusoidalVariation() {
        Point2D result = VariationApplier.apply(VariationType.SINUSOIDAL, new Point2D(0.5, -0.25));
        assertClose(Math.sin(0.5), result.x());
        assertClose(Math.sin(-0.25), result.y());
    }

    @Test
    void appliesSphericalVariation() {
        Point2D result = VariationApplier.apply(VariationType.SPHERICAL, new Point2D(1.0, 2.0));
        assertClose(0.2, result.x());
        assertClose(0.4, result.y());
    }

    @Test
    void appliesSwirlVariation() {
        Point2D point = new Point2D(1.0, 0.5);
        double radiusSquared = 1.25;
        Point2D result = VariationApplier.apply(VariationType.SWIRL, point);
        assertClose(point.x() * Math.sin(radiusSquared) - point.y() * Math.cos(radiusSquared), result.x());
        assertClose(point.x() * Math.cos(radiusSquared) + point.y() * Math.sin(radiusSquared), result.y());
    }

    @Test
    void appliesHorseshoeVariation() {
        Point2D point = new Point2D(1.0, 2.0);
        double radius = Math.sqrt(5.0);
        Point2D result = VariationApplier.apply(VariationType.HORSESHOE, point);
        assertClose(-3.0 / radius, result.x());
        assertClose(4.0 / radius, result.y());
    }

    private void assertClose(double expected, double actual) {
        assertEquals(expected, actual, EPSILON);
    }
}
