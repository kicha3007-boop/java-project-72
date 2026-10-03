package hexlet.code.service;

public final class UrlCheckException extends RuntimeException {

    public UrlCheckException(String message) {
        super(message);
    }

    public UrlCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
