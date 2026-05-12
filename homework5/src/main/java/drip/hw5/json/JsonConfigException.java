package drip.hw5.json;

public class JsonConfigException extends RuntimeException {
    public JsonConfigException(String message) {
        super(message);
    }

    public JsonConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
