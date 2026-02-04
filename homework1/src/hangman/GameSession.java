package hangman;

import java.util.HashSet;
import java.util.Set;

public class GameSession {
    private final String answer;
    private final char[] progress;
    private final int maxErrors;
    private int errors;
    private final Set<Character> guessed = new HashSet<>();

    public GameSession(String answer, int maxErrors) {
        this.answer = answer.toLowerCase();
        this.maxErrors = maxErrors;
        this.progress = new char[answer.length()];
        for (int i = 0; i < progress.length; i++) {
            progress[i] = '_';
        }
    }

    public boolean guess(char ch) {
        ch = Character.toLowerCase(ch);
        if (guessed.contains(ch)) {
            return true;
        }
        guessed.add(ch);
        boolean hit = false;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == ch) {
                progress[i] = ch;
                hit = true;
            }
        }
        if (!hit) {
            errors++;
        }
        return hit;
    }

    public boolean isWin() {
        for (char c : progress) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    public boolean isLose() {
        return errors >= maxErrors;
    }

    public String progressString() {
        String result = "";
        for (int i = 0; i < progress.length; i++) {
            result += progress[i];
            if (i < progress.length - 1) {
                result += " ";
            }
        }
        return result;
    }


    public int getErrors() {
        return errors;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public String getAnswer() {
        return answer;
    }
}
