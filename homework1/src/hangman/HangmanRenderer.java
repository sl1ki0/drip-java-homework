package hangman;

public class HangmanRenderer {
    private final String[] stages = {
            """
             +---+
                 |
                 |
                 |
                ===
            """,
            """
             +---+
             O   |
                 |
                 |
                ===
            """,
            """
             +---+
             O   |
             |   |
                 |
                ===
            """,
            """
             +---+
             O   |
            /|   |
                 |
                ===
            """,
            """
             +---+
             O   |
            /|\\  |
                 |
                ===
            """,
            """
             +---+
             O   |
            /|\\  |
            /    |
                ===
            """,
            """
             +---+
             O   |
            /|\\  |
            / \\  |
                ===
            """
    };

    public String render(int errors, int maxErrors) {
        if (maxErrors <= 0) {
            return stages[stages.length - 1];
        }
        if (errors < 0) {
            return stages[0];
        }
        if (errors >= stages.length) {
            return stages[stages.length - 1];
        }
        return stages[errors];
    }
}
