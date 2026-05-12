package drip.hw6.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CsvParser {
    private static final String COLUMN_SEPARATOR = ",";

    public <T> List<T> parseFromCsv(String filename, Class<T> klass) {
        List<CsvColumn> columns = CsvSchema.describe(klass);
        Constructor<T> constructor = noArgConstructor(klass);

        try (BufferedReader reader = CsvStreams.openReader(filename)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new CsvException("CSV file is empty: " + filename);
            }
            CsvColumn[] columnsByPosition = matchHeader(headerLine, columns);

            List<T> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                result.add(parseRow(line, constructor, columnsByPosition));
            }
            return result;
        } catch (IOException exception) {
            throw new CsvException("Failed to read CSV: " + filename, exception);
        }
    }

    public <T> void saveToCsv(String filename, Collection<T> collection, Class<T> klass) {
        List<CsvColumn> columns = CsvSchema.describe(klass);

        try (PrintWriter writer = CsvStreams.openWriter(filename)) {
            writer.println(joinHeader(columns));
            for (T item : collection) {
                writer.println(joinRow(item, columns));
            }
            writer.flush();
            if (writer.checkError()) {
                throw new CsvException("Error while writing CSV: " + filename);
            }
        } catch (IOException exception) {
            throw new CsvException("Failed to write CSV: " + filename, exception);
        }
    }

    private static <T> T parseRow(String line, Constructor<T> constructor, CsvColumn[] columnsByPosition) {
        String[] cells = line.split(COLUMN_SEPARATOR, -1);
        T instance;
        try {
            instance = constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new CsvException("Failed to create instance of " + constructor.getDeclaringClass().getName(),
                exception);
        }
        for (int i = 0; i < columnsByPosition.length; i++) {
            CsvColumn column = columnsByPosition[i];
            if (column == null) {
                continue;
            }
            String raw = i < cells.length ? cells[i] : "";
            column.readInto(instance, raw);
        }
        return instance;
    }

    private static CsvColumn[] matchHeader(String headerLine, List<CsvColumn> columns) {
        Map<String, CsvColumn> byHeader = new HashMap<>();
        for (CsvColumn column : columns) {
            byHeader.put(column.header(), column);
        }
        String[] headerNames = headerLine.split(COLUMN_SEPARATOR, -1);
        CsvColumn[] ordered = new CsvColumn[headerNames.length];
        for (int i = 0; i < headerNames.length; i++) {
            ordered[i] = byHeader.get(headerNames[i].trim());
        }
        return ordered;
    }

    private static String joinHeader(List<CsvColumn> columns) {
        StringJoiner joiner = new StringJoiner(COLUMN_SEPARATOR);
        for (CsvColumn column : columns) {
            joiner.add(column.header());
        }
        return joiner.toString();
    }

    private static String joinRow(Object item, List<CsvColumn> columns) {
        StringJoiner joiner = new StringJoiner(COLUMN_SEPARATOR);
        for (CsvColumn column : columns) {
            joiner.add(column.writeFrom(item));
        }
        return joiner.toString();
    }

    private static <T> Constructor<T> noArgConstructor(Class<T> klass) {
        try {
            Constructor<T> constructor = klass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException exception) {
            throw new CsvException("Class %s must declare a no-arg constructor".formatted(klass.getName()), exception);
        }
    }
}
