package drip.hw4.service;

import drip.hw4.cache.ApiCache;
import drip.hw4.cache.CachedResponse;
import drip.hw4.client.SpaceXApiException;
import drip.hw4.client.SpaceXHttpClient;
import drip.hw4.json.JsonBuilder;
import drip.hw4.json.JsonParser;
import drip.hw4.json.JsonParsingException;
import drip.hw4.model.Launch;
import drip.hw4.model.LaunchQueryResponse;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class SpaceXLaunchService {
    private static final String BASE_URL = "https://api.spacexdata.com";
    private static final String ALL_LAUNCHES_URL = BASE_URL + "/v5/launches";
    private static final String LATEST_LAUNCH_URL = BASE_URL + "/v5/launches/latest";
    private static final String QUERY_LAUNCHES_URL = BASE_URL + "/v5/launches/query";
    private static final DateTimeFormatter CACHE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final SpaceXHttpClient httpClient;
    private final JsonParser jsonParser;
    private final JsonBuilder jsonBuilder;
    private final ApiCache apiCache;

    public SpaceXLaunchService(
            SpaceXHttpClient httpClient,
            JsonParser jsonParser,
            JsonBuilder jsonBuilder,
            ApiCache apiCache
    ) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
        this.apiCache = apiCache;
    }

    public ServiceResponse<List<Launch>> getAllLaunches() throws SpaceXServiceException {
        return executeCachedRequest(
                "launches_all.json",
                () -> httpClient.get(ALL_LAUNCHES_URL),
                jsonParser::parseLaunchList,
                "получении списка запусков",
                "списка запусков"
        );
    }

    public ServiceResponse<Launch> getLatestLaunch() throws SpaceXServiceException {
        return executeCachedRequest(
                "launches_latest.json",
                () -> httpClient.get(LATEST_LAUNCH_URL),
                jsonParser::parseLaunch,
                "получении последнего запуска",
                "последнего запуска"
        );
    }

    public ServiceResponse<List<Launch>> findLaunchesByDate(LocalDate startDate, LocalDate endDate)
            throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildDateRangeQuery(startDate, endDate);
        String cacheFileName = "query_" + startDate + "_" + endDate + ".json";
        return executeCachedRequest(
                cacheFileName,
                () -> httpClient.post(QUERY_LAUNCHES_URL, requestBody),
                this::parseQueryLaunches,
                "поиске запусков по дате",
                "результатов поиска запусков по дате"
        );
    }

    public ServiceResponse<List<Launch>> getSuccessfulLaunches() throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildSuccessQuery(true);
        return executeCachedRequest(
                "query_success_true.json",
                () -> httpClient.post(QUERY_LAUNCHES_URL, requestBody),
                this::parseQueryLaunches,
                "получении успешных запусков",
                "успешных запусков"
        );
    }

    public ServiceResponse<List<Launch>> getFailedLaunches() throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildSuccessQuery(false);
        return executeCachedRequest(
                "query_success_false.json",
                () -> httpClient.post(QUERY_LAUNCHES_URL, requestBody),
                this::parseQueryLaunches,
                "получении неудачных запусков",
                "неудачных запусков"
        );
    }

    public void clearCache() throws SpaceXServiceException {
        try {
            apiCache.clear();
        } catch (IOException e) {
            throw new SpaceXServiceException("Не удалось очистить кеш.", e);
        }
    }

    private List<Launch> parseQueryLaunches(String json) throws JsonParsingException {
        LaunchQueryResponse queryResponse = jsonParser.parseLaunchQueryResponse(json);
        return queryResponse.getDocs();
    }

    private <T> ServiceResponse<T> executeCachedRequest(
            String cacheFileName,
            RequestExecutor requestExecutor,
            ResponseParser<T> parser,
            String operationName,
            String dataDescription
    ) throws SpaceXServiceException {
        Optional<ServiceResponse<T>> cachedResponse = tryLoadValidCache(cacheFileName, parser);
        if (cachedResponse.isPresent()) {
            return cachedResponse.get();
        }

        try {
            String responseBody = requestExecutor.execute();
            T data = parser.parse(responseBody);
            apiCache.save(cacheFileName, responseBody);
            return ServiceResponse.withoutNotice(data);
        } catch (IOException e) {
            return getFallbackOrThrow(
                    cacheFileName,
                    parser,
                    "Сетевая ошибка при " + operationName + ".",
                    e
            );
        } catch (SpaceXApiException e) {
            return getFallbackOrThrow(
                    cacheFileName,
                    parser,
                    "Ошибка API при " + operationName + ": " + e.getMessage(),
                    e
            );
        } catch (JsonParsingException e) {
            return getFallbackOrThrow(
                    cacheFileName,
                    parser,
                    "Сервер вернул некорректные данные для " + dataDescription + ".",
                    e
            );
        }
    }

    private <T> Optional<ServiceResponse<T>> tryLoadValidCache(String cacheFileName, ResponseParser<T> parser) {
        try {
            Optional<CachedResponse> cachedResponse = apiCache.getValidCache(cacheFileName);
            if (cachedResponse.isEmpty()) {
                return Optional.empty();
            }

            T data = parser.parse(cachedResponse.get().body());
            return Optional.of(ServiceResponse.withoutNotice(data));
        } catch (IOException | JsonParsingException e) {
            return Optional.empty();
        }
    }

    private <T> ServiceResponse<T> getFallbackOrThrow(
            String cacheFileName,
            ResponseParser<T> parser,
            String errorMessage,
            Exception cause
    ) throws SpaceXServiceException {
        try {
            Optional<CachedResponse> cachedResponse = apiCache.getValidCache(cacheFileName);
            if (cachedResponse.isPresent()) {
                T data = parser.parse(cachedResponse.get().body());
                String notice = buildCacheFallbackNotice(cachedResponse.get().savedAt());
                return ServiceResponse.withNotice(data, notice);
            }
        } catch (IOException | JsonParsingException e) {
            throw new SpaceXServiceException(errorMessage, cause);
        }

        throw new SpaceXServiceException(errorMessage, cause);
    }

    private String buildCacheFallbackNotice(Instant savedAt) {
        return "[!] Сервер недоступен. Показаны данные из кеша (сохранены "
                + CACHE_DATE_FORMATTER.format(savedAt)
                + ")";
    }

    @FunctionalInterface
    private interface RequestExecutor {
        String execute() throws IOException, SpaceXApiException;
    }

    @FunctionalInterface
    private interface ResponseParser<T> {
        T parse(String json) throws JsonParsingException;
    }
}
