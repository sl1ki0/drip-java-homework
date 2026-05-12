package drip.hw6.csv;

import drip.hw6.csv.model.AllScalars;
import drip.hw6.csv.model.Person;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvParserBasicTest {
    @TempDir
    Path tempDir;

    private final CsvParser parser = new CsvParser();

    @Test
    void parsesPlainCsvWithCsvNameAnnotation() throws IOException {
        Path csv = tempDir.resolve("people.csv");
        Files.writeString(csv, """
            id,full_name,age
            1,Ivan Ivanov,25
            2,Maria Petrova,30
            """);

        List<Person> people = parser.parseFromCsv(csv.toString(), Person.class);

        assertEquals(List.of(
            new Person(1, "Ivan Ivanov", 25),
            new Person(2, "Maria Petrova", 30)
        ), people);
    }

    @Test
    void savesPlainCsvWithExpectedHeaderAndRows() throws IOException {
        Path csv = tempDir.resolve("out.csv");
        List<Person> people = List.of(
            new Person(1, "Ivan Ivanov", 25),
            new Person(2, "Maria Petrova", 30)
        );

        parser.saveToCsv(csv.toString(), people, Person.class);

        List<String> lines = Files.readAllLines(csv);
        assertEquals("id,full_name,age", lines.get(0));
        assertEquals("1,Ivan Ivanov,25", lines.get(1));
        assertEquals("2,Maria Petrova,30", lines.get(2));
    }

    @Test
    void roundTripsAllSupportedScalarTypes() {
        Path csv = tempDir.resolve("scalars.csv");
        List<AllScalars> rows = List.of(
            new AllScalars(1, 2, 3L, 4L, 1.5, 2.5, true, false, "hello"),
            new AllScalars(-7, null, 9L, null, -0.25, null, false, null, "")
        );

        parser.saveToCsv(csv.toString(), rows, AllScalars.class);
        List<AllScalars> loaded = parser.parseFromCsv(csv.toString(), AllScalars.class);

        assertEquals(rows, loaded);
    }

    @Test
    void toleratesUnknownColumnsInTheHeader() throws IOException {
        Path csv = tempDir.resolve("extra.csv");
        Files.writeString(csv, """
            id,full_name,age,department
            1,Ivan Ivanov,25,QA
            """);

        List<Person> people = parser.parseFromCsv(csv.toString(), Person.class);

        assertEquals(1, people.size());
        assertEquals(new Person(1, "Ivan Ivanov", 25), people.get(0));
    }

    @Test
    void handlesShuffledHeaderOrder() throws IOException {
        Path csv = tempDir.resolve("shuffled.csv");
        Files.writeString(csv, """
            full_name,age,id
            Ivan Ivanov,25,1
            """);

        List<Person> people = parser.parseFromCsv(csv.toString(), Person.class);

        assertEquals(new Person(1, "Ivan Ivanov", 25), people.get(0));
    }

    @Test
    void throwsOnMissingNoArgConstructorWhenParsing() throws IOException {
        Path csv = tempDir.resolve("any.csv");
        Files.writeString(csv, "x\n1\n");
        assertThrows(CsvException.class,
            () -> parser.parseFromCsv(csv.toString(), NoNoArg.class));
    }

    @Test
    void throwsOnEmptyFile() throws IOException {
        Path csv = tempDir.resolve("empty.csv");
        Files.writeString(csv, "");
        assertThrows(CsvException.class,
            () -> parser.parseFromCsv(csv.toString(), Person.class));
    }

    static class NoNoArg {
        private int x;

        @SuppressWarnings("unused")
        NoNoArg(int x) {
            this.x = x;
        }
    }
}
