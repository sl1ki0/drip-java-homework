package drip.hw6.csv.model;

import drip.hw6.csv.CsvCollection;
import java.util.List;
import java.util.Objects;

public class Course {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course other)) return false;
        return Objects.equals(name, other.name)
            && Objects.equals(tags, other.tags)
            && Objects.equals(authors, other.authors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tags, authors);
    }
}
