package drip.hw6.csv.model;

import drip.hw6.csv.CsvName;
import java.util.Objects;

public class Employee {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return id == other.id && Objects.equals(name, other.name) && Objects.equals(address, other.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }
}
