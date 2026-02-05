package hangman;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final int MIN_WORD = 3;
    private static final int MAX_WORD = 12;

    public static void main(String[] args) {
        WordDictionary dictionary = new WordDictionary();
        String category = null;
        String difficulty = null;

        for (String arg : args) {
            if (arg.startsWith("--category=")) {
                category = arg.substring("--category=".length());
            } else if (arg.startsWith("--difficulty=")) {
                difficulty = arg.substring("--difficulty=".length());
            }
        }

        Scanner scanner = new Scanner(System.in);

        if (category == null) {
            category = readCategory(scanner, dictionary);
        }
        if (category == null || !dictionary.hasCategory(category)) {
            category = dictionary.randomCategory();
        }

        if (difficulty == null) {
            difficulty = readDifficulty(scanner);
        }
        if (difficulty == null || !isValidDifficulty(difficulty)) {
            difficulty = randomDifficulty();
        }

        String word = dictionary.randomWord(category, difficulty);
        if (word == null || word.length() < MIN_WORD || word.length() > MAX_WORD) {
            System.out.println("Слово некорректной длины, игра не запускается.");
            return;
        }

        int maxErrors = errorsForDifficulty(difficulty);
        GameSession session = new GameSession(word, maxErrors);
        HangmanRenderer renderer = new HangmanRenderer();
        String hint = dictionary.hintFor(word);
        boolean hintUsed = false;

        System.out.println("Категория: " + category);
        System.out.println("Сложность: " + difficulty);
        System.out.println("Попыток: " + maxErrors);

        while (!session.isWin() && !session.isLose()) {
            System.out.println();
            System.out.println(renderer.render(session.getErrors(), session.getMaxErrors()));
            System.out.println("Слово: " + session.progressString());
            System.out.println("Осталось попыток: " + (session.getMaxErrors() - session.getErrors()));
            System.out.print("Введите букву (или '?' для подсказки): ");

            String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if ("?".equals(input)) {
                if (hint == null) {
                    System.out.println("Подсказка недоступна.");
                } else if (hintUsed) {
                    System.out.println("Подсказка уже использована.");
                } else {
                    System.out.println("Подсказка: " + hint);
                    hintUsed = true;
                }
                continue;
            }
            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                System.out.println("Введите одну букву.");
                continue;
            }

            boolean hit = session.guess(input.charAt(0));
            if (!hit) {
                System.out.println("Неверная буква.");
            }
        }

        System.out.println();
        System.out.println(renderer.render(session.getErrors(), session.getMaxErrors()));
        if (session.isWin()) {
            System.out.println("Вы выиграли! Слово: " + session.getAnswer());
        } else {
            System.out.println("Вы проиграли. Слово: " + session.getAnswer());
        }
    }

    private static String readCategory(Scanner scanner, WordDictionary dictionary) {
        System.out.print("Введите категорию (Enter — случайная): ");
        String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        if (input.isEmpty()) {
            return null;
        }
        return input;
    }

    private static String readDifficulty(Scanner scanner) {
        System.out.print("Введите сложность (easy/medium/hard) (Enter — случайная): ");
        String input = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        if (input.isEmpty()) {
            return null;
        }
        return input;
    }

    private static boolean isValidDifficulty(String difficulty) {
        return "easy".equals(difficulty) || "medium".equals(difficulty) || "hard".equals(difficulty);
    }

    // Тут мб правильней было enum заюзать, но я оставлю так в этот раз
    private static String randomDifficulty() {
        String[] difficulties = { "easy", "medium", "hard" };
        return difficulties[new java.util.Random().nextInt(difficulties.length)];
    }


    private static int errorsForDifficulty(String difficulty) {
        if ("easy".equals(difficulty)) {
            return 8;
        }
        if ("medium".equals(difficulty)) {
            return 6;
        }
        return 5;
    }
}
