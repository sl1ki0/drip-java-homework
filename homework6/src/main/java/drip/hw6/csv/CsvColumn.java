package drip.hw6.csv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

final class CsvColumn {
    private final String header;
    private final List<Field> parentFields;
    private final Field leaf;
    private final boolean list;
    private final Class<?> elementType;
    private final String listDelimiter;

    private CsvColumn(String header, List<Field> parentFields, Field leaf,
                     boolean list, Class<?> elementType, String listDelimiter) {
        this.header = header;
        this.parentFields = List.copyOf(parentFields);
        this.leaf = leaf;
        this.list = list;
        this.elementType = elementType;
        this.listDelimiter = listDelimiter;
        this.leaf.setAccessible(true);
        for (Field field : this.parentFields) {
            field.setAccessible(true);
        }
    }

    static CsvColumn scalar(String header, List<Field> parentFields, Field leaf) {
        return new CsvColumn(header, parentFields, leaf, false, null, null);
    }

    static CsvColumn list(String header, List<Field> parentFields, Field leaf,
                          Class<?> elementType, String listDelimiter) {
        return new CsvColumn(header, parentFields, leaf, true, elementType, listDelimiter);
    }

    String header() {
        return header;
    }

    void readInto(Object root, String rawCell) {
        try {
            Object owner = ensureOwner(root);
            Object value = list
                ? parseList(rawCell)
                : CsvValues.parseScalar(rawCell, leaf.getType());
            leaf.set(owner, value);
        } catch (ReflectiveOperationException exception) {
            throw new CsvException("Failed to assign field '%s'".formatted(header), exception);
        }
    }

    String writeFrom(Object root) {
        try {
            Object owner = findOwner(root);
            if (owner == null) {
                return "";
            }
            Object value = leaf.get(owner);
            if (list) {
                return formatList(value);
            }
            return CsvValues.formatScalar(value);
        } catch (ReflectiveOperationException exception) {
            throw new CsvException("Failed to read field '%s'".formatted(header), exception);
        }
    }

    private Object ensureOwner(Object root) throws ReflectiveOperationException {
        Object current = root;
        for (Field field : parentFields) {
            Object next = field.get(current);
            if (next == null) {
                next = field.getType().getDeclaredConstructor().newInstance();
                field.set(current, next);
            }
            current = next;
        }
        return current;
    }

    private Object findOwner(Object root) throws ReflectiveOperationException {
        Object current = root;
        for (Field field : parentFields) {
            current = field.get(current);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private List<Object> parseList(String rawCell) {
        if (rawCell == null || rawCell.isEmpty()) {
            return new ArrayList<>();
        }
        String[] tokens = rawCell.split(Pattern.quote(listDelimiter), -1);
        List<Object> result = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            result.add(CsvValues.parseScalar(token, elementType));
        }
        return result;
    }

    private String formatList(Object value) {
        if (value == null) {
            return "";
        }
        if (!(value instanceof List<?> list)) {
            throw new CsvException("Expected List for column '%s', got %s"
                .formatted(header, value.getClass().getName()));
        }
        StringJoiner joiner = new StringJoiner(listDelimiter);
        for (Object element : list) {
            joiner.add(CsvValues.formatScalar(element));
        }
        return joiner.toString();
    }

    static List<Field> emptyPath() {
        return Collections.emptyList();
    }
}
