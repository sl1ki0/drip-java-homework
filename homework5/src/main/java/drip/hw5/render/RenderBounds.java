package drip.hw5.render;

record RenderBounds(double minX, double maxX, double minY, double maxY) {
    double width() {
        return maxX - minX;
    }

    double height() {
        return maxY - minY;
    }

    RenderBounds withPadding(double ratio) {
        double xPadding = width() * ratio;
        double yPadding = height() * ratio;
        return new RenderBounds(minX - xPadding, maxX + xPadding, minY - yPadding, maxY + yPadding);
    }
}
