package drip.hw3;

import drip.hw3.argumentParser.ArgumentParser;
import drip.hw3.argumentParser.ParsedArguments;

public class Main {
    public static void main(String[] args) {
        ParsedArguments parsedArguments;
        try {
            parsedArguments = ArgumentParser.parseArguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
            System.exit(1);
        }
    }
}
