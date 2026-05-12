package drip.hw6.csv;

import drip.hw6.csv.model.Course;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvParserListTest {
    @TempDir
    Path tempDir;

    private final CsvParser parser = new CsvParser();

    @Test
    void readsListsWithCustomAndDefaultDelimiters() throws IOException {
        Path csv = tempDir.resolve("courses.csv");
        Files.writeString(csv, """
            name,tags,authors
            Java Basics,oop;collections;streams,Gandalf|Frodo
            Spring Basics,spring;boot;web,Arathorn|Legolas
            """);

        List<Course> courses = parser.parseFromCsv(csv.toString(), Course.class);

        assertEquals(List.of(
            new Course("Java Basics",
                List.of("oop", "collections", "streams"),
                List.of("Gandalf", "Frodo")),
            new Course("Spring Basics",
                List.of("spring", "boot", "web"),
                List.of("Arathorn", "Legolas"))
        ), courses);
    }

    @Test
    void writesListsWithCustomAndDefaultDelimiters() throws IOException {
        Path csv = tempDir.resolve("courses.csv");
        List<Course> courses = List.of(
            new Course("Java Basics",
                List.of("oop", "collections", "streams"),
                List.of("Gandalf", "Frodo"))
        );

        parser.saveToCsv(csv.toString(), courses, Course.class);

        List<String> lines = Files.readAllLines(csv);
        assertEquals("name,tags,authors", lines.get(0));
        assertEquals("Java Basics,oop;collections;streams,Gandalf|Frodo", lines.get(1));
    }

    @Test
    void roundTripsEmptyListAsEmptyCell() {
        Path csv = tempDir.resolve("courses.csv");
        List<Course> courses = List.of(
            new Course("Empty", List.of(), List.of("solo"))
        );

        parser.saveToCsv(csv.toString(), courses, Course.class);
        List<Course> loaded = parser.parseFromCsv(csv.toString(), Course.class);

        assertEquals(courses, loaded);
    }
}
