package hangman;

import java.util.Map;

public final class WordData {
    private WordData() {
    }

    public static Map<String, String[]> categories() {
        return Map.of(
                "животные", new String[]{"кот", "собака", "тигр", "заяц"},
                "еда", new String[]{"хлеб", "сыр", "банан", "пицца"},
                "спорт", new String[]{"теннис", "футбол", "хоккей", "баскетбол"}
        );
    }

    public static Map<String, String> hints() {
        return Map.ofEntries(
                Map.entry("кот", "домашний питомец, мяукает"),
                Map.entry("собака", "домашний питомец, лает"),
                Map.entry("тигр", "полосатый хищник"),
                Map.entry("заяц", "быстрое животное с длинными ушами"),
                Map.entry("хлеб", "то, что едят с супом"),
                Map.entry("сыр", "молочный продукт"),
                Map.entry("банан", "желтый фрукт"),
                Map.entry("пицца", "круглая итальянская еда"),
                Map.entry("теннис", "спорт с ракеткой и мячом"),
                Map.entry("футбол", "командная игра с мячом"),
                Map.entry("хоккей", "игра на льду с шайбой"),
                Map.entry("баскетбол", "игра с мячом и кольцом")
        );
    }
}
