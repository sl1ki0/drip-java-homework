package drip.hw5.render;

import drip.hw5.config.AffineTransformConfig;

record PreparedAffineTransform(AffineTransformConfig transform, RgbColor color) {
    Point2D apply(Point2D point) {
        return new Point2D(
            transform.a() * point.x() + transform.b() * point.y() + transform.c(),
            transform.d() * point.x() + transform.e() * point.y() + transform.f()
        );
    }
}
