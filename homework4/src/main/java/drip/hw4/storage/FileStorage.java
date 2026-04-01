package drip.hw4.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileStorage {
    public void write(Path path, String content) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    public String read(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!content.isEmpty()) {
                    content.append(System.lineSeparator());
                }
                content.append(line);
            }

            return content.toString();
        }
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }

    public void deleteIfExists(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public List<Path> listFiles(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return List.of();
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .toList();
        }
    }
}
