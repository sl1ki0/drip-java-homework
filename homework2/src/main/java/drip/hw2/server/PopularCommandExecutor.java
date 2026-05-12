package main.java.drip.hw2.server;

public final class PopularCommandExecutor {
    private final ConnectionManager manager;
    private final int maxAttempts;

    public PopularCommandExecutor(ConnectionManager manager, int maxAttempts) {
        if (manager == null) {
            throw new IllegalArgumentException("manager must not be null");
        }
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive. are you okay, bro? wtf...");
        }
        this.manager = manager;
        this.maxAttempts = maxAttempts;
    }

    public void updatePackages() {
        tryExecute("apt update && apt upgrade -y");

    }

    void tryExecute(String command) {
        ConnectionException lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try (Connection connection = manager.getConnection()) {
                connection.execute(command);
                return;
            } catch (ConnectionException e) {
                lastError = e;
            }
        }

        throw new ConnectionException("Execution failed after " + maxAttempts + " attempts", lastError);
    }
}
