package hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WordDictionary {
    private final Map<String, String[]> categories;
    private final Random random = new Random();

    public WordDictionary() {
        categories = Map.of(
                "animals", new String[]{"cat", "dog", "elephant", "giraffe", "tiger", "wolf", "rabbit", "dolphin"},
                "food", new String[]{"bread", "pizza", "carrot", "banana", "pasta", "tomato", "cheese", "strawberry"},
                "sports", new String[]{"tennis", "football", "boxing", "swimming", "hockey", "cycling", "basketball"},
                "tech", new String[]{"keyboard", "monitor", "network", "program", "internet", "processor", "database"}
        );
    }

    public String randomCategory() {
        List<String> keys = new ArrayList<>(categories.keySet());
        return keys.get(random.nextInt(keys.size()));
    }

    public boolean hasCategory(String category) {
        return categories.containsKey(category);
    }

    public String randomWord(String category, String difficulty) {
        String[] words = categories.get(category);
        if (words == null || words.length == 0) {
            return null;
        }

        List<String> filtered = new ArrayList<>();
        for (String w : words) {
            if (matchesDifficulty(w, difficulty)) {
                filtered.add(w);
            }
        }

        if (filtered.isEmpty()) {
            return words[random.nextInt(words.length)];
        }

        return filtered.get(random.nextInt(filtered.size()));
    }

    private boolean matchesDifficulty(String word, String difficulty) {
        int len = word.length();
        if ("easy".equals(difficulty)) {
            return len >= 3 && len <= 5;
        }
        if ("medium".equals(difficulty)) {
            return len >= 6 && len <= 8;
        }
        if ("hard".equals(difficulty)) {
            return len >= 9;
        }
        return true;
    }
}
