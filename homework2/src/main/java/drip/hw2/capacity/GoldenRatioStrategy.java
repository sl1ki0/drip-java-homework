package main.java.drip.hw2.capacity;

public class GoldenRatioStrategy implements CapacityStrategy {
    private static final double GOLDEN_RATIO = 1.618;

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int newCapacity = currentCapacity;
        if (newCapacity <= 0) {
            newCapacity = 1;
        }

        while (newCapacity < requiredCapacity) {
            newCapacity = (int) Math.ceil(newCapacity * GOLDEN_RATIO);
        }

        return newCapacity;
    }
}
