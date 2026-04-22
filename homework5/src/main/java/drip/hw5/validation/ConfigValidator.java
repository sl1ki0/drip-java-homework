package drip.hw5.validation;

import drip.hw5.config.AffineTransformConfig;
import drip.hw5.config.RenderConfig;
import drip.hw5.config.VariationConfig;
import java.util.List;

public final class ConfigValidator {
    public void validate(RenderConfig config) {
        require(config.size().width() > 0, "Width must be greater than 0.");
        require(config.size().height() > 0, "Height must be greater than 0.");
        require(config.iterationCount() > 0, "Iteration count must be greater than 0.");
        require(config.threads() > 0, "Thread count must be greater than 0.");
        require(config.symmetryLevel() >= 1, "Symmetry level must be at least 1.");
        require(config.outputPath() != null && !config.outputPath().toString().isBlank(), "Output path must not be blank.");

        validateFunctions(config.functions());
        validateAffineParams(config.affineParams());
    }

    private void validateFunctions(List<VariationConfig> functions) {
        require(functions != null && !functions.isEmpty(), "At least one variation must be configured.");
        for (VariationConfig function : functions) {
            require(function.name() != null, "Variation name must not be null.");
            require(function.weight() > 0.0, "Variation weight must be greater than 0.");
        }
    }

    private void validateAffineParams(List<AffineTransformConfig> transforms) {
        require(transforms != null && !transforms.isEmpty(), "At least one affine transform must be configured.");
        for (AffineTransformConfig transform : transforms) {
            require(isFinite(transform.a()), "Affine coefficient a must be finite.");
            require(isFinite(transform.b()), "Affine coefficient b must be finite.");
            require(isFinite(transform.c()), "Affine coefficient c must be finite.");
            require(isFinite(transform.d()), "Affine coefficient d must be finite.");
            require(isFinite(transform.e()), "Affine coefficient e must be finite.");
            require(isFinite(transform.f()), "Affine coefficient f must be finite.");
        }
    }

    private boolean isFinite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new ConfigValidationException(message);
        }
    }
}
