package drip.hw3;

import drip.hw3.argumentParser.ArgumentParser;
import drip.hw3.argumentParser.ParsedArguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {
        try {
            ParsedArguments parsedArguments = ArgumentParser.parseArguments(args);

            AppLogger.info("Запуск анализа логов");

            LogAnalysisResult result = new LogAnalyzer().analyze(parsedArguments);
            String report = ReportRenderer.render(result, parsedArguments);

            Files.writeString(parsedArguments.output(), report, StandardOpenOption.CREATE_NEW);

            AppLogger.info("Отчет сохранен в " + parsedArguments.output());
        } catch (IllegalArgumentException e) {
            AppLogger.warn(e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            AppLogger.warn("Ошибка при работе с файлами: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            AppLogger.warn("Непредвиденная ошибка: " + e.getMessage());
            System.exit(1);
        }
    }
}
