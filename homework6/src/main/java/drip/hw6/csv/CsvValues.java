package drip.hw6.csv;

import java.util.Set;

final class CsvValues {
    private static final Set<Class<?>> SCALAR_TYPES = Set.of(
        int.class, Integer.class,
        long.class, Long.class,
        double.class, Double.class,
        boolean.class, Boolean.class,
        String.class
    );

    private CsvValues() {
    }

    static boolean isSupportedScalar(Class<?> type) {
        return SCALAR_TYPES.contains(type);
    }

    static Object parseScalar(String raw, Class<?> type) {
        if (type == String.class) {
            return raw;
        }
        if (raw == null || raw.isEmpty()) {
            return defaultPrimitive(type);
        }
        try {
            if (type == int.class || type == Integer.class) {
                return Integer.parseInt(raw);
            }
            if (type == long.class || type == Long.class) {
                return Long.parseLong(raw);
            }
            if (type == double.class || type == Double.class) {
                return Double.parseDouble(raw);
            }
            if (type == boolean.class || type == Boolean.class) {
                return Boolean.parseBoolean(raw);
            }
        } catch (NumberFormatException exception) {
            throw new CsvException("Cannot parse value '%s' as %s".formatted(raw, type.getSimpleName()), exception);
        }
        throw new CsvException("Unsupported scalar type: " + type.getName());
    }

    static String formatScalar(Object value) {
        return value == null ? "" : value.toString();
    }

    private static Object defaultPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == double.class) {
            return 0.0;
        }
        if (type == boolean.class) {
            return false;
        }
        throw new CsvException("Unsupported primitive type: " + type.getName());
    }
}
