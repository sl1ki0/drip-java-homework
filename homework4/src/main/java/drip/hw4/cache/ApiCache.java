package drip.hw4.cache;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import drip.hw4.storage.FileStorage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ApiCache {
    private static final Type METADATA_TYPE = new TypeToken<Map<String, String>>() {
    }.getType();

    private final Path cacheDirectory;
    private final Path metadataFile;
    private final Duration ttl;
    private final FileStorage fileStorage;
    private final Gson gson = new Gson();

    public ApiCache(Path cacheDirectory, Duration ttl, FileStorage fileStorage) {
        this.cacheDirectory = cacheDirectory;
        this.metadataFile = cacheDirectory.resolve("cache_meta.json");
        this.ttl = ttl;
        this.fileStorage = fileStorage;
    }

    public Optional<CachedResponse> getValidCache(String fileName) throws IOException {
        Path cacheFile = cacheDirectory.resolve(fileName);
        if (fileStorage.isMissing(cacheFile)) {
            return Optional.empty();
        }

        Map<String, String> metadata = readMetadata();
        String savedAtValue = metadata.get(fileName);
        if (savedAtValue == null) {
            return Optional.empty();
        }

        Instant savedAt = parseSavedAt(savedAtValue);
        if (savedAt == null || isExpired(savedAt)) {
            return Optional.empty();
        }

        return Optional.of(new CachedResponse(fileStorage.read(cacheFile), savedAt));
    }

    public void save(String fileName, String body) throws IOException {
        Path cacheFile = cacheDirectory.resolve(fileName);
        fileStorage.write(cacheFile, body);

        Map<String, String> metadata = readMetadata();
        metadata.put(fileName, Instant.now().toString());
        fileStorage.write(metadataFile, gson.toJson(metadata));
    }

    public void clear() throws IOException {
        for (Path file : fileStorage.listFiles(cacheDirectory)) {
            fileStorage.deleteIfExists(file);
        }
    }

    private Map<String, String> readMetadata() throws IOException {
        if (fileStorage.isMissing(metadataFile)) {
            return new LinkedHashMap<>();
        }

        String json = fileStorage.read(metadataFile);
        if (json.isBlank()) {
            return new LinkedHashMap<>();
        }

        try {
            Map<String, String> metadata = gson.fromJson(json, METADATA_TYPE);
            return metadata == null ? new LinkedHashMap<>() : new LinkedHashMap<>(metadata);
        } catch (JsonSyntaxException e) {
            return new LinkedHashMap<>();
        }
    }

    private Instant parseSavedAt(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean isExpired(Instant savedAt) {
        return savedAt.plus(ttl).isBefore(Instant.now());
    }
}
