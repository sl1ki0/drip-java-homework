package main.java.drip.hw2.server;

public interface Connection extends AutoCloseable {
    void execute(String command);

    @Override
    void close();
}
