package drip.hw4.service;

import drip.hw4.client.SpaceXApiException;
import drip.hw4.client.SpaceXHttpClient;
import drip.hw4.json.JsonBuilder;
import drip.hw4.json.JsonParser;
import drip.hw4.json.JsonParsingException;
import drip.hw4.model.Launch;
import drip.hw4.model.LaunchQueryResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class SpaceXLaunchService {
    private static final String BASE_URL = "https://api.spacexdata.com";
    private static final String ALL_LAUNCHES_URL = BASE_URL + "/v5/launches";
    private static final String LATEST_LAUNCH_URL = BASE_URL + "/v5/launches/latest";
    private static final String QUERY_LAUNCHES_URL = BASE_URL + "/v5/launches/query";

    private final SpaceXHttpClient httpClient;
    private final JsonParser jsonParser;
    private final JsonBuilder jsonBuilder;

    public SpaceXLaunchService(SpaceXHttpClient httpClient, JsonParser jsonParser, JsonBuilder jsonBuilder) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
    }

    public List<Launch> getAllLaunches() throws SpaceXServiceException {
        try {
            String response = httpClient.get(ALL_LAUNCHES_URL);
            return jsonParser.parseLaunchList(response);
        } catch (IOException e) {
            throw new SpaceXServiceException("Сетевая ошибка при получении списка запусков.", e);
        } catch (SpaceXApiException e) {
            throw new SpaceXServiceException("Ошибка API при получении списка запусков: " + e.getMessage(), e);
        } catch (JsonParsingException e) {
            throw new SpaceXServiceException("Сервер вернул некорректные данные для списка запусков.", e);
        }
    }

    public Launch getLatestLaunch() throws SpaceXServiceException {
        try {
            String response = httpClient.get(LATEST_LAUNCH_URL);
            return jsonParser.parseLaunch(response);
        } catch (IOException e) {
            throw new SpaceXServiceException("Сетевая ошибка при получении последнего запуска.", e);
        } catch (SpaceXApiException e) {
            throw new SpaceXServiceException("Ошибка API при получении последнего запуска: " + e.getMessage(), e);
        } catch (JsonParsingException e) {
            throw new SpaceXServiceException("Сервер вернул некорректные данные для последнего запуска.", e);
        }
    }

    public List<Launch> findLaunchesByDate(LocalDate startDate, LocalDate endDate) throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildDateRangeQuery(startDate, endDate);
        return executeQuery(requestBody, "поиске запусков по дате");
    }

    public List<Launch> getSuccessfulLaunches() throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildSuccessQuery(true);
        return executeQuery(requestBody, "получении успешных запусков");
    }

    public List<Launch> getFailedLaunches() throws SpaceXServiceException {
        String requestBody = jsonBuilder.buildSuccessQuery(false);
        return executeQuery(requestBody, "получении неудачных запусков");
    }

    private List<Launch> executeQuery(String requestBody, String operationName) throws SpaceXServiceException {
        try {
            String response = httpClient.post(QUERY_LAUNCHES_URL, requestBody);
            LaunchQueryResponse queryResponse = jsonParser.parseLaunchQueryResponse(response);
            return queryResponse.getDocs();
        } catch (IOException e) {
            throw new SpaceXServiceException("Сетевая ошибка при " + operationName + ".", e);
        } catch (SpaceXApiException e) {
            throw new SpaceXServiceException("Ошибка API при " + operationName + ": " + e.getMessage(), e);
        } catch (JsonParsingException e) {
            throw new SpaceXServiceException("Сервер вернул некорректные данные при " + operationName + ".", e);
        }
    }
}
