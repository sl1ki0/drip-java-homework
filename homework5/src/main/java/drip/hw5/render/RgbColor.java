package drip.hw5.render;

import java.awt.Color;

record RgbColor(double red, double green, double blue) {
    RgbColor blend(RgbColor other) {
        return new RgbColor(
            (red + other.red) / 2.0,
            (green + other.green) / 2.0,
            (blue + other.blue) / 2.0
        );
    }

    static RgbColor black() {
        return new RgbColor(0.0, 0.0, 0.0);
    }

    static RgbColor fromHue(int index, int total) {
        float hue = total <= 0 ? 0.0f : (float) index / total;
        Color color = Color.getHSBColor(hue, 0.85f, 1.0f);
        return new RgbColor(
            color.getRed() / 255.0,
            color.getGreen() / 255.0,
            color.getBlue() / 255.0
        );
    }
}
