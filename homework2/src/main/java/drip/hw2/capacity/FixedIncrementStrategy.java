package main.java.drip.hw2.capacity;

public class FixedIncrementStrategy implements CapacityStrategy {
    private final int increment;

    public FixedIncrementStrategy(int increment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("increment must be positive");
        }
        this.increment = increment;
    }

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int newCapacity = currentCapacity;
        if (newCapacity <= 0) {
            newCapacity = 1;
        }

        while (newCapacity < requiredCapacity) {
            newCapacity += increment;
        }

        return newCapacity;
    }
}
