package drip.hw5.json;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.RenderConfigDefaults;
import drip.hw5.config.ImageSize;
import drip.hw5.config.VariationConfig;
import drip.hw5.config.VariationType;
import drip.hw5.util.SeedNormalizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class JsonConfigLoader {
    private final Gson gson = new Gson();

    public RenderConfig load(Path path) {
        try {
            String content = Files.readString(path);
            JsonRenderConfig rawConfig = gson.fromJson(content, JsonRenderConfig.class);
            if (rawConfig == null) {
                throw new JsonConfigException("JSON config is empty: " + path);
            }
            return mapConfig(rawConfig);
        } catch (IOException exception) {
            throw new JsonConfigException("Failed to read JSON config: " + path, exception);
        } catch (JsonParseException | IllegalArgumentException exception) {
            throw new JsonConfigException("Failed to parse JSON config %s: %s".formatted(path, exception.getMessage()), exception);
        }
    }

    private RenderConfig mapConfig(JsonRenderConfig rawConfig) {
        RenderConfig defaults = RenderConfigDefaults.defaultConfig();

        return new RenderConfig(
            new ImageSize(
                rawConfig.size != null && rawConfig.size.width != null ? rawConfig.size.width : defaults.size().width(),
                rawConfig.size != null && rawConfig.size.height != null ? rawConfig.size.height : defaults.size().height()
            ),
            rawConfig.iterationCount != null ? rawConfig.iterationCount : defaults.iterationCount(),
            rawConfig.outputPath != null ? Path.of(rawConfig.outputPath) : defaults.outputPath(),
            rawConfig.threads != null ? rawConfig.threads : defaults.threads(),
            rawConfig.seed != null ? SeedNormalizer.normalize(rawConfig.seed.toString()) : defaults.seed(),
            rawConfig.functions != null ? mapFunctions(rawConfig.functions) : defaults.functions(),
            rawConfig.affineParams != null ? mapAffineParams(rawConfig.affineParams) : defaults.affineParams(),
            rawConfig.symmetryLevel != null ? rawConfig.symmetryLevel : defaults.symmetryLevel()
        );
    }

    private List<VariationConfig> mapFunctions(List<JsonRenderConfig.JsonVariationConfig> rawFunctions) {
        return rawFunctions.stream()
            .map(function -> new VariationConfig(
                VariationType.fromName(function.name),
                function.weight != null ? function.weight : Double.NaN
            ))
            .toList();
    }

    private List<AffineTransformConfig> mapAffineParams(List<JsonRenderConfig.JsonAffineTransformConfig> rawTransforms) {
        return rawTransforms.stream()
            .map(transform -> new AffineTransformConfig(
                orNaN(transform.a),
                orNaN(transform.b),
                orNaN(transform.c),
                orNaN(transform.d),
                orNaN(transform.e),
                orNaN(transform.f)
            ))
            .toList();
    }

    private double orNaN(Double value) {
        return value != null ? value : Double.NaN;
    }
}
