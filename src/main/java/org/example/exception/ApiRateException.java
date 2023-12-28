package org.example.exception;

public class ApiRateException extends RuntimeException {

    public ApiRateException() {
    }

    public ApiRateException(String message) {
        super(message);
    }
}
