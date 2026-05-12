package drip.hw5.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PngImageWriterTest {
    @TempDir
    Path tempDir;

    @Test
    void writesPngFile() throws IOException {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        image.setRGB(5, 5, 0xFF8844);

        Path output = tempDir.resolve("image.png");
        new PngImageWriter().write(image, output);

        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0L);
    }
}
