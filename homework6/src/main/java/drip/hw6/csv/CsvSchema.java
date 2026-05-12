package drip.hw6.csv;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class CsvSchema {
    private CsvSchema() {
    }

    static List<CsvColumn> describe(Class<?> klass) {
        List<CsvColumn> columns = new ArrayList<>();
        collect(klass, "", CsvColumn.emptyPath(), new HashSet<>(), columns);
        if (columns.isEmpty()) {
            throw new CsvException("Class %s has no readable fields".formatted(klass.getName()));
        }
        return columns;
    }

    private static void collect(Class<?> klass, String prefix, List<Field> path,
                                Set<Class<?>> stack, List<CsvColumn> out) {
        if (!stack.add(klass)) {
            throw new CsvException("Cyclic nesting detected at class " + klass.getName());
        }
        try {
            for (Field field : klass.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || field.isSynthetic()) {
                    continue;
                }
                String header = headerOf(field, prefix);
                Class<?> type = field.getType();

                if (CsvValues.isSupportedScalar(type)) {
                    out.add(CsvColumn.scalar(header, path, field));
                } else if (List.class.isAssignableFrom(type)) {
                    out.add(CsvColumn.list(header, path, field,
                        resolveListElementType(field), delimiterOf(field)));
                } else {
                    List<Field> nextPath = new ArrayList<>(path);
                    nextPath.add(field);
                    collect(type, header, nextPath, stack, out);
                }
            }
        } finally {
            stack.remove(klass);
        }
    }

    private static String headerOf(Field field, String prefix) {
        CsvName annotation = field.getAnnotation(CsvName.class);
        String name = annotation != null ? annotation.value() : field.getName();
        return prefix.isEmpty() ? name : prefix + "." + name;
    }

    private static String delimiterOf(Field field) {
        CsvCollection annotation = field.getAnnotation(CsvCollection.class);
        return annotation != null ? annotation.delimiter() : "|";
    }

    private static Class<?> resolveListElementType(Field field) {
        Type generic = field.getGenericType();
        if (generic instanceof ParameterizedType parameterized) {
            Type[] args = parameterized.getActualTypeArguments();
            if (args.length == 1 && args[0] instanceof Class<?> elementClass) {
                if (!CsvValues.isSupportedScalar(elementClass)) {
                    throw new CsvException(
                        "Unsupported list element type %s for field '%s'"
                            .formatted(elementClass.getName(), field.getName()));
                }
                return elementClass;
            }
        }
        throw new CsvException(
            "Cannot determine element type for list field '%s' (must be parameterised)"
                .formatted(field.getName()));
    }
}
