import model.Book;
import orm.core.OrmException;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Book.class);

            em.saveAll(List.of(
                    new Book("Война и мир", "Лев Толстой", 1869, true),
                    new Book("Анна Каренина", "Лев Толстой", 1877, true),
                    new Book("Преступление и наказание", "Фёдор Достоевский", 1866, true),
                    new Book("Идиот", "Фёдор Достоевский", 1869, false),
                    new Book("Отцы и дети", "Иван Тургенев", 1862, true)
            ));
            System.out.println("Сохранено книг: " + em.count(Book.class));

            System.out.println("Книги Льва Толстого:");
            List<Book> tolstoy = em.findAllWhere(Book.class, "author", "Лев Толстой");
            for (Book book : tolstoy) {
                System.out.println("  " + book);
            }

            Book first = tolstoy.get(0);
            first.setAvailable(false);
            System.out.println("Обновлено строк: " + em.update(first));

            Optional<Book> idiot = em.findOneWhere(Book.class, "title", "Идиот");
            System.out.println("findOneWhere(title='Идиот') = " + idiot.orElse(null));

            System.out.println("Удалено строк: " + em.delete(first));
            System.out.println("Книг после удаления: " + em.count(Book.class));

            long before = em.count(Book.class);
            try {
                em.saveAll(Arrays.asList(
                        new Book("Нормальная книга", "Автор", 2000, true),
                        new Book(null, "Автор без названия", 2001, true)
                ));
            } catch (OrmException e) {
                System.out.println("saveAll откатился: " + e.getMessage());
            }
            long after = em.count(Book.class);
            System.out.println("Книг до пачки: " + before + ", после: " + after + " (не изменилось)");
        }
    }
}
