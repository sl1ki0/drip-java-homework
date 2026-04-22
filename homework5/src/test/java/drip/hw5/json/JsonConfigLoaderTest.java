package drip.hw5.json;

import drip.hw5.config.RenderConfig;
import drip.hw5.config.VariationType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonConfigLoaderTest {
    @TempDir
    Path tempDir;

    @Test
    void loadsJsonAndFillsMissingFieldsFromDefaults() throws IOException {
        Path configPath = tempDir.resolve("config.json");
        Files.writeString(configPath, """
            {
              "size": {
                "width": 900
              },
              "threads": 3,
              "functions": [
                {
                  "name": "swirl",
                  "weight": 1.25
                }
              ],
              "affine_params": [
                {
                  "a": 1.0,
                  "b": 0.0,
                  "c": 0.0,
                  "d": 0.0,
                  "e": 1.0,
                  "f": 0.0
                }
              ],
              "symmetry_level": 4
            }
            """);

        RenderConfig config = new JsonConfigLoader().load(configPath);

        assertEquals(900, config.size().width());
        assertEquals(1080, config.size().height());
        assertEquals(2500, config.iterationCount());
        assertEquals(3, config.threads());
        assertEquals(VariationType.SWIRL, config.functions().getFirst().name());
        assertEquals(4, config.symmetryLevel());
    }

    @Test
    void loadsFloatingPointSeed() throws IOException {
        Path configPath = tempDir.resolve("seed.json");
        Files.writeString(configPath, """
            {
              "seed": 2.1324512
            }
            """);

        RenderConfig config = new JsonConfigLoader().load(configPath);

        assertEquals(Double.doubleToLongBits(2.1324512d), config.seed());
    }
}
