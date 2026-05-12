package drip.hw5.util;

public final class SeedNormalizer {
    private SeedNormalizer() {
    }

    public static long normalize(String rawSeed) {
        try {
            if (rawSeed.indexOf('.') >= 0 || rawSeed.indexOf('e') >= 0 || rawSeed.indexOf('E') >= 0) {
                return Double.doubleToLongBits(Double.parseDouble(rawSeed));
            }
            return Long.parseLong(rawSeed);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Seed must be a valid integer or floating-point number: " + rawSeed, exception);
        }
    }
}
