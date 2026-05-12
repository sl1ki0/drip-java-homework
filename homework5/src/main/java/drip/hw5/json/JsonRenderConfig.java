package drip.hw5.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class JsonRenderConfig {
    public JsonSize size;
    @SerializedName("iteration_count")
    public Integer iterationCount;
    @SerializedName("output_path")
    public String outputPath;
    public Integer threads;
    public Object seed;
    public List<JsonVariationConfig> functions;
    @SerializedName("affine_params")
    public List<JsonAffineTransformConfig> affineParams;
    @SerializedName("symmetry_level")
    public Integer symmetryLevel;

    public static final class JsonSize {
        public Integer width;
        public Integer height;
    }

    public static final class JsonVariationConfig {
        public String name;
        public Double weight;
    }

    public static final class JsonAffineTransformConfig {
        public Double a;
        public Double b;
        public Double c;
        public Double d;
        public Double e;
        public Double f;
    }
}
