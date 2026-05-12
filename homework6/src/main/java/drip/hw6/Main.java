package drip.hw6;

import drip.hw6.csv.CsvCollection;
import drip.hw6.csv.CsvName;
import drip.hw6.csv.CsvParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try {
            Path workDir = Path.of(args.length > 0 ? args[0] : "out");
            Files.createDirectories(workDir);

            CsvParser parser = new CsvParser();
            runPersonDemo(parser, workDir);
            runEmployeeDemo(parser, workDir);
            runCourseDemo(parser, workDir);

            System.out.println("Done. CSV files are in: " + workDir.toAbsolutePath());
        } catch (Exception exception) {
            System.err.println("Unexpected error in Main: " + exception.getMessage());
            exception.printStackTrace(System.err);
        }
    }

    private static void runPersonDemo(CsvParser parser, Path workDir) throws IOException {
        Path plainCsv = workDir.resolve("people.csv");
        Files.writeString(plainCsv, """
            id,full_name,age
            1,Ivan Ivanov,25
            2,Maria Petrova,30
            """);

        List<Person> people = parser.parseFromCsv(plainCsv.toString(), Person.class);
        System.out.println("[plain csv] " + people);

        Path gzipCsv = workDir.resolve("people.csv.gz");
        parser.saveToCsv(gzipCsv.toString(), people, Person.class);
        System.out.println("[gzip    ] " + parser.parseFromCsv(gzipCsv.toString(), Person.class));

        Path zipCsv = workDir.resolve("people.zip");
        parser.saveToCsv(zipCsv.toString(), people, Person.class);
        System.out.println("[zip     ] " + parser.parseFromCsv(zipCsv.toString(), Person.class));
    }

    private static void runEmployeeDemo(CsvParser parser, Path workDir) {
        Path csv = workDir.resolve("employees.csv");
        List<Employee> employees = List.of(
            new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
            new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        );

        parser.saveToCsv(csv.toString(), employees, Employee.class);
        System.out.println("[nested  ] " + parser.parseFromCsv(csv.toString(), Employee.class));
    }

    private static void runCourseDemo(CsvParser parser, Path workDir) {
        Path csv = workDir.resolve("courses.csv");
        List<Course> courses = List.of(
            new Course("Java Basics",
                List.of("oop", "collections", "streams"),
                List.of("Gandalf", "Frodo")),
            new Course("Spring Basics",
                List.of("spring", "boot", "web"),
                List.of("Arathorn", "Legolas"))
        );

        parser.saveToCsv(csv.toString(), courses, Course.class);
        System.out.println("[list    ] " + parser.parseFromCsv(csv.toString(), Course.class));
    }

    public static class Person {
        private int id;
        @CsvName("full_name")
        private String name;
        private int age;

        public Person() {
        }

        public Person(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{id=%d, name='%s', age=%d}".formatted(id, name, age);
        }
    }

    public static class Employee {
        private int id;
        @CsvName("full_name")
        private String name;
        private Address address;

        public Employee() {
        }

        public Employee(int id, String name, Address address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return "Employee{id=%d, name='%s', address=%s}".formatted(id, name, address);
        }
    }

    public static class Address {
        private String street;
        private int flat;

        public Address() {
        }

        public Address(String street, int flat) {
            this.street = street;
            this.flat = flat;
        }

        @Override
        public String toString() {
            return "Address{street='%s', flat=%d}".formatted(street, flat);
        }
    }

    public static class Course {
        private String name;
        @CsvCollection(delimiter = ";")
        private List<String> tags;
        private List<String> authors;

        public Course() {
        }

        public Course(String name, List<String> tags, List<String> authors) {
            this.name = name;
            this.tags = tags;
            this.authors = authors;
        }

        @Override
        public String toString() {
            return "Course{name='%s', tags=%s, authors=%s}".formatted(name, tags, authors);
        }
    }
}
