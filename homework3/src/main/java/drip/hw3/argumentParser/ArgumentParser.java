package drip.hw3.argumentParser;

import java.nio.file.Path;
import java.util.List;

public final class ArgumentParser {
    String arguments;

    List<Path> paths;
    String format;
    String output;
    String from;
    String to;

    private ArgumentParser(){}

    static List<ParsedArguments> parseArguments(String[] args){
        try {
            List<Path> paths = InputPathParser.resolvePath(args[0]);
        } catch (IllegalArgumentException e) {

        }
    }
}
