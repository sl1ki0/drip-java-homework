package main.java.drip.hw2.capacity;

public class DoublingStrategy implements CapacityStrategy {
    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int newCapacity = currentCapacity;
        if (newCapacity <= 0) {
            newCapacity = 1;
        }

        while (newCapacity < requiredCapacity) {
            newCapacity *= 2;
        }

        return newCapacity;
    }
}
