package drip.hw5.render;

import java.awt.image.BufferedImage;

public final class FlameImageBuffer {
    private static final double GAMMA = 2.2;

    private final int width;
    private final int height;
    private final long[] hits;
    private final double[] redSums;
    private final double[] greenSums;
    private final double[] blueSums;

    public FlameImageBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        this.hits = new long[size];
        this.redSums = new double[size];
        this.greenSums = new double[size];
        this.blueSums = new double[size];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void addPoint(int x, int y, RgbColor color) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        int index = y * width + x;
        hits[index]++;
        redSums[index] += color.red();
        greenSums[index] += color.green();
        blueSums[index] += color.blue();
    }

    public void merge(FlameImageBuffer other) {
        for (int index = 0; index < hits.length; index++) {
            hits[index] += other.hits[index];
            redSums[index] += other.redSums[index];
            greenSums[index] += other.greenSums[index];
            blueSums[index] += other.blueSums[index];
        }
    }

    public boolean hasHits() {
        for (long hit : hits) {
            if (hit > 0) {
                return true;
            }
        }
        return false;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        long maxHits = 0L;
        for (long hit : hits) {
            if (hit > maxHits) {
                maxHits = hit;
            }
        }

        if (maxHits == 0L) {
            return image;
        }

        double maxLogDensity = Math.log1p(maxHits);
        for (int index = 0; index < hits.length; index++) {
            long hit = hits[index];
            if (hit == 0L) {
                continue;
            }

            double brightness = Math.pow(Math.log1p(hit) / maxLogDensity, 1.0 / GAMMA);
            int red = clampToByte((redSums[index] / hit) * brightness * 255.0);
            int green = clampToByte((greenSums[index] / hit) * brightness * 255.0);
            int blue = clampToByte((blueSums[index] / hit) * brightness * 255.0);

            int x = index % width;
            int y = index / width;
            image.setRGB(x, y, (red << 16) | (green << 8) | blue);
        }

        return image;
    }

    private int clampToByte(double value) {
        return (int) Math.max(0, Math.min(255, Math.round(value)));
    }
}
