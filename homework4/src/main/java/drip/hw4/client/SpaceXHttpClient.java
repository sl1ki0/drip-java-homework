package drip.hw4.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SpaceXHttpClient {
    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 10_000;

    public String get(String url) throws IOException, SpaceXApiException {
        HttpURLConnection connection = openConnection(url, "GET");
        return execute(connection, null);
    }

    public String post(String url, String jsonBody) throws IOException, SpaceXApiException {
        HttpURLConnection connection = openConnection(url, "POST");
        connection.setRequestProperty("Content-Type", "application/json");
        return execute(connection, jsonBody);
    }

    private HttpURLConnection openConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(READ_TIMEOUT_MILLIS);
        return connection;
    }

    private String execute(HttpURLConnection connection, String requestBody) throws IOException, SpaceXApiException {
        try {
            if (requestBody != null) {
                connection.setDoOutput(true);
                try (var outputStream = connection.getOutputStream()) {
                    outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            int statusCode = connection.getResponseCode();
            String responseBody = readResponseBody(connection, statusCode);
            return handleResponse(statusCode, responseBody);
        } finally {
            connection.disconnect();
        }
    }

    String handleResponse(int statusCode, String responseBody) throws SpaceXApiException {
        if (statusCode == HttpURLConnection.HTTP_OK) {
            return responseBody;
        }

        throw new SpaceXApiException(statusCode, responseBody);
    }

    private String readResponseBody(HttpURLConnection connection, int statusCode) throws IOException {
        InputStream stream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (stream == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!body.isEmpty()) {
                    body.append(System.lineSeparator());
                }
                body.append(line);
            }

            return body.toString();
        }
    }
}
