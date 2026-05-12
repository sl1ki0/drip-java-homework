package drip.hw4.service;

public class ServiceResponse<T> {
    private final T data;
    private final String notice;

    private ServiceResponse(T data, String notice) {
        this.data = data;
        this.notice = notice;
    }

    public static <T> ServiceResponse<T> withoutNotice(T data) {
        return new ServiceResponse<>(data, null);
    }

    public static <T> ServiceResponse<T> withNotice(T data, String notice) {
        return new ServiceResponse<>(data, notice);
    }

    public T getData() {
        return data;
    }

    public String getNotice() {
        return notice;
    }

    public boolean hasNotice() {
        return notice != null && !notice.isBlank();
    }
}
