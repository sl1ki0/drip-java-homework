package drip.hw6.csv.model;

import java.util.Objects;

public class AllScalars {
    private int primitiveInt;
    private Integer boxedInt;
    private long primitiveLong;
    private Long boxedLong;
    private double primitiveDouble;
    private Double boxedDouble;
    private boolean primitiveBoolean;
    private Boolean boxedBoolean;
    private String text;

    public AllScalars() {
    }

    public AllScalars(int primitiveInt, Integer boxedInt,
                      long primitiveLong, Long boxedLong,
                      double primitiveDouble, Double boxedDouble,
                      boolean primitiveBoolean, Boolean boxedBoolean,
                      String text) {
        this.primitiveInt = primitiveInt;
        this.boxedInt = boxedInt;
        this.primitiveLong = primitiveLong;
        this.boxedLong = boxedLong;
        this.primitiveDouble = primitiveDouble;
        this.boxedDouble = boxedDouble;
        this.primitiveBoolean = primitiveBoolean;
        this.boxedBoolean = boxedBoolean;
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AllScalars other)) return false;
        return primitiveInt == other.primitiveInt
            && primitiveLong == other.primitiveLong
            && Double.compare(primitiveDouble, other.primitiveDouble) == 0
            && primitiveBoolean == other.primitiveBoolean
            && Objects.equals(boxedInt, other.boxedInt)
            && Objects.equals(boxedLong, other.boxedLong)
            && Objects.equals(boxedDouble, other.boxedDouble)
            && Objects.equals(boxedBoolean, other.boxedBoolean)
            && Objects.equals(text, other.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primitiveInt, boxedInt, primitiveLong, boxedLong,
            primitiveDouble, boxedDouble, primitiveBoolean, boxedBoolean, text);
    }
}
