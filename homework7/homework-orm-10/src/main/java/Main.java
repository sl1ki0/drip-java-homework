import model.RegistrationForm;
import orm.core.ValidationException;
import orm.core.Violation;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(RegistrationForm.class);

            RegistrationForm valid = new RegistrationForm("ivan", "secret123", 25);
            Long id = em.save(valid);
            System.out.println("Сохранена валидная форма, id = " + id);

            RegistrationForm invalid = new RegistrationForm("ab", null, 15);
            try {
                em.save(invalid);
            } catch (ValidationException e) {
                System.out.println("Невалидная форма, нарушения:");
                printViolations(e);
            }

            List<RegistrationForm> batch = Arrays.asList(
                    new RegistrationForm("petr", "password1", 30),
                    new RegistrationForm("xx", "short", 200)
            );
            long before = em.count(RegistrationForm.class);
            try {
                em.saveAll(batch);
            } catch (ValidationException e) {
                System.out.println("saveAll откатился из-за невалидной формы, нарушения:");
                printViolations(e);
            }
            long after = em.count(RegistrationForm.class);
            System.out.println("Форм до пачки: " + before + ", после: " + after + " (не изменилось)");
        }
    }

    private static void printViolations(ValidationException e) {
        for (Violation violation : e.getViolations()) {
            System.out.println("  - поле '" + violation.field() + "': " + violation.message()
                    + " (значение = " + violation.invalidValue() + ")");
        }
    }
}
