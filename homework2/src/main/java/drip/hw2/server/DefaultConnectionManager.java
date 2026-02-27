package main.java.drip.hw2.server;

import java.util.Random;

public class DefaultConnectionManager implements ConnectionManager {
    private final static Random random = new Random();


    @Override
    public Connection getConnection() {
        if (random.nextDouble() > 0.5) {
            return new FaultyConnection();
        }
        return new StableConnection();
    }
}
