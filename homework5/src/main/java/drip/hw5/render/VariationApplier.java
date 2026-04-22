package drip.hw5.render;

import drip.hw5.config.VariationType;

final class VariationApplier {
    private static final double EPSILON = 1.0e-12;

    private VariationApplier() {
    }

    static Point2D apply(VariationType type, Point2D point) {
        return switch (type) {
            case LINEAR -> point;
            case SINUSOIDAL -> new Point2D(Math.sin(point.x()), Math.sin(point.y()));
            case SPHERICAL -> spherical(point);
            case SWIRL -> swirl(point);
            case HORSESHOE -> horseshoe(point);
        };
    }

    private static Point2D spherical(Point2D point) {
        double radiusSquared = point.x() * point.x() + point.y() * point.y();
        if (radiusSquared < EPSILON) {
            return new Point2D(0.0, 0.0);
        }
        return new Point2D(point.x() / radiusSquared, point.y() / radiusSquared);
    }

    private static Point2D swirl(Point2D point) {
        double radiusSquared = point.x() * point.x() + point.y() * point.y();
        double sin = Math.sin(radiusSquared);
        double cos = Math.cos(radiusSquared);
        return new Point2D(
            point.x() * sin - point.y() * cos,
            point.x() * cos + point.y() * sin
        );
    }

    private static Point2D horseshoe(Point2D point) {
        double radiusSquared = point.x() * point.x() + point.y() * point.y();
        double radius = Math.sqrt(radiusSquared);
        if (radius < EPSILON) {
            return new Point2D(0.0, 0.0);
        }
        return new Point2D(
            ((point.x() - point.y()) * (point.x() + point.y())) / radius,
            (2.0 * point.x() * point.y()) / radius
        );
    }
}
