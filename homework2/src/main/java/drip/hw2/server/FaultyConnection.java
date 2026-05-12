package main.java.drip.hw2.server;

import java.util.Random;

public class FaultyConnection implements Connection {
    private final static Random random = new Random();

    @Override
    public void execute(String command) {
        if (random.nextBoolean()) {
            throw new ConnectionException("Failed to execute command");
        }
    }

    @Override
    public void close() {
    }
}
