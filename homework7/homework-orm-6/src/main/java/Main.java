import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Student.class);

            Student ivan = new Student("Иван Иванов", 20, 4.5);
            Student petr = new Student("Пётр Петров", 22, 4.8);
            Student anna = new Student("Анна Сидорова", 19, 5.0);

            Long id1 = em.save(ivan);
            Long id2 = em.save(petr);
            Long id3 = em.save(anna);

            System.out.println("Сгенерированные id:");
            System.out.println(id1);
            System.out.println(id2);
            System.out.println(id3);

            System.out.println("ivan.getId() = " + ivan.getId());
        }
    }
}
