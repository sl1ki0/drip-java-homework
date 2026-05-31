import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Student.class);

            Long id1 = em.save(new Student("Иван Иванов", 20, 4.5, LocalDate.of(2021, 9, 1)));
            Long id2 = em.save(new Student("Пётр Петров", 22, 4.8, LocalDate.of(2022, 9, 1)));
            em.save(new Student("Анна Сидорова", 19, 5.0, LocalDate.of(2023, 9, 1)));

            Optional<Student> found = em.findById(Student.class, id1);
            System.out.println("findById(" + id1 + ") = " + found.orElse(null));

            System.out.println("findAll:");
            List<Student> all = em.findAll(Student.class);
            for (Student student : all) {
                System.out.println("  " + student);
            }

            Optional<Student> missing = em.findById(Student.class, 999L);
            System.out.println("findById(999) isPresent = " + missing.isPresent());

            System.out.println("второй сохранённый id = " + id2);
        }
    }
}
