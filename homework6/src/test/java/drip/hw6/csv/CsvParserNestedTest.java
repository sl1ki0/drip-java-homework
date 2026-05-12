package drip.hw6.csv;

import drip.hw6.csv.model.Address;
import drip.hw6.csv.model.Company;
import drip.hw6.csv.model.Employee;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvParserNestedTest {
    @TempDir
    Path tempDir;

    private final CsvParser parser = new CsvParser();

    @Test
    void readsNestedObjectWithDottedHeaders() throws IOException {
        Path csv = tempDir.resolve("employees.csv");
        Files.writeString(csv, """
            id,full_name,address.street,address.flat
            1,Ivan Ivanov,Lenina 10,42
            2,Maria Petrova,Pushkina 5,7
            """);

        List<Employee> employees = parser.parseFromCsv(csv.toString(), Employee.class);

        assertEquals(List.of(
            new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
            new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        ), employees);
    }

    @Test
    void writesNestedObjectWithDottedHeaders() throws IOException {
        Path csv = tempDir.resolve("employees.csv");
        List<Employee> employees = List.of(
            new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
            new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        );

        parser.saveToCsv(csv.toString(), employees, Employee.class);

        List<String> lines = Files.readAllLines(csv);
        assertEquals("id,full_name,address.street,address.flat", lines.get(0));
        assertEquals("1,Ivan Ivanov,Lenina 10,42", lines.get(1));
        assertEquals("2,Maria Petrova,Pushkina 5,7", lines.get(2));
    }

    @Test
    void supportsArbitraryNestingDepth() {
        Path csv = tempDir.resolve("company.csv");
        List<Company> companies = List.of(
            new Company("Acme", new Employee(7, "Boss", new Address("Main 1", 100)))
        );

        parser.saveToCsv(csv.toString(), companies, Company.class);
        List<Company> loaded = parser.parseFromCsv(csv.toString(), Company.class);

        assertEquals(companies, loaded);
    }
}
