package model;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

@Table(name = "books")
public class Book {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column
    private int publicationYear;

    @Column
    private boolean available;

    public Book() {
    }

    public Book(String title, String author, int publicationYear, boolean available) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author
                + "', publicationYear=" + publicationYear + ", available=" + available + "}";
    }
}
