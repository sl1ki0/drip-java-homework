package drip.hw6.csv.model;

import java.util.Objects;

public class Address {
    private String street;
    private int flat;

    public Address() {
    }

    public Address(String street, int flat) {
        this.street = street;
        this.flat = flat;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getFlat() {
        return flat;
    }

    public void setFlat(int flat) {
        this.flat = flat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address other)) return false;
        return flat == other.flat && Objects.equals(street, other.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, flat);
    }
}
