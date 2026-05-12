package drip.hw6.csv.model;

import java.util.Objects;

public class Company {
    private String title;
    private Employee chief;

    public Company() {
    }

    public Company(String title, Employee chief) {
        this.title = title;
        this.chief = chief;
    }

    public String getTitle() {
        return title;
    }

    public Employee getChief() {
        return chief;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company other)) return false;
        return Objects.equals(title, other.title) && Objects.equals(chief, other.chief);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, chief);
    }
}
