package drip.hw4.client;

public class SpaceXApiException extends Exception {
    public SpaceXApiException(int statusCode, String responseBody) {
        super("HTTP " + statusCode + ": " + responseBody);
    }
}
