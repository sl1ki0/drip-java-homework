package drip.hw5.config;

public enum VariationType {
    LINEAR("linear"),
    SINUSOIDAL("sinusoidal"),
    SPHERICAL("spherical"),
    SWIRL("swirl"),
    HORSESHOE("horseshoe");

    private final String cliName;

    VariationType(String cliName) {
        this.cliName = cliName;
    }

    public String cliName() {
        return cliName;
    }

    public static VariationType fromName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            throw new IllegalArgumentException("Variation name must not be blank.");
        }
        for (VariationType value : values()) {
            if (value.cliName.equalsIgnoreCase(rawName)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported variation: " + rawName);
    }
}
