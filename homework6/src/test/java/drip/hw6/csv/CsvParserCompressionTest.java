package drip.hw6.csv;

import drip.hw6.csv.model.Person;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvParserCompressionTest {
    @TempDir
    Path tempDir;

    private final CsvParser parser = new CsvParser();

    private final List<Person> sample = List.of(
        new Person(1, "Ivan Ivanov", 25),
        new Person(2, "Maria Petrova", 30)
    );

    @Test
    void roundTripsThroughGzip() throws IOException {
        Path gz = tempDir.resolve("people.csv.gz");

        parser.saveToCsv(gz.toString(), sample, Person.class);

        try (InputStream raw = Files.newInputStream(gz);
             GZIPInputStream gzip = new GZIPInputStream(raw)) {
            String content = new String(gzip.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(content.startsWith("id,full_name,age"));
        }

        List<Person> loaded = parser.parseFromCsv(gz.toString(), Person.class);
        assertEquals(sample, loaded);
    }

    @Test
    void roundTripsThroughGzWithoutCsvSuffix() throws IOException {
        Path gz = tempDir.resolve("people.gz");

        parser.saveToCsv(gz.toString(), sample, Person.class);
        List<Person> loaded = parser.parseFromCsv(gz.toString(), Person.class);

        assertEquals(sample, loaded);
    }

    @Test
    void roundTripsThroughZip() throws IOException {
        Path zip = tempDir.resolve("people.zip");

        parser.saveToCsv(zip.toString(), sample, Person.class);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry = zis.getNextEntry();
            assertNotNull(entry);
            assertTrue(entry.getName().toLowerCase().endsWith(".csv"));
        }

        List<Person> loaded = parser.parseFromCsv(zip.toString(), Person.class);
        assertEquals(sample, loaded);
    }
}
