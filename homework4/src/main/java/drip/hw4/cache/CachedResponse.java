package drip.hw4.cache;

import java.time.Instant;

public class CachedResponse {
    private final String body;
    private final Instant savedAt;

    public CachedResponse(String body, Instant savedAt) {
        this.body = body;
        this.savedAt = savedAt;
    }

    public String getBody() {
        return body;
    }

    public Instant getSavedAt() {
        return savedAt;
    }
}
