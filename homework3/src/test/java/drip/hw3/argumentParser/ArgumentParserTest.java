package drip.hw3.argumentParser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArgumentParserTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldParseRequiredArguments() throws IOException {
        Path inputLog = Files.writeString(tempDir.resolve("access.log"), "test");
        Path outputFile = tempDir.resolve("report.json");

        ParsedArguments arguments = ArgumentParser.parseArguments(new String[]{
                "--path", inputLog.toString(),
                "--format", "json",
                "--output", outputFile.toString(),
                "--from", "2025-03-01",
                "--to", "2025-03-02"
        });

        assertEquals(1, arguments.paths().size());
        assertEquals(inputLog.toAbsolutePath().normalize(), arguments.paths().get(0));
        assertEquals("json", arguments.format());
        assertEquals(outputFile.normalize(), arguments.output());
        assertEquals("2025-03-01", arguments.from());
        assertEquals("2025-03-02", arguments.to());
    }

    @Test
    void shouldFailOnUnsupportedFormat() throws IOException {
        Path inputLog = Files.writeString(tempDir.resolve("access.log"), "test");
        Path outputFile = tempDir.resolve("report.txt");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ArgumentParser.parseArguments(new String[]{
                        "--path", inputLog.toString(),
                        "--format", "xml",
                        "--output", outputFile.toString()
                })
        );

        assertEquals("Неподдерживаемый формат вывода: xml", exception.getMessage());
    }
}
