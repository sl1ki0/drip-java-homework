package drip.hw4.cache;

import java.time.Instant;

public record CachedResponse(String body, Instant savedAt) {
}
