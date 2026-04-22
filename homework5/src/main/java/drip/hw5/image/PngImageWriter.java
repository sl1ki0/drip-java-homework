package drip.hw5.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class PngImageWriter {
    public void write(BufferedImage image, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ImageIO.write(image, "png", outputPath.toFile());
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write PNG image to " + outputPath, exception);
        }
    }
}
