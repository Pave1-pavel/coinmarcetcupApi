package org.example.exception;

public class ApiMonthRateException extends RuntimeException {
    public ApiMonthRateException() {
    }

    public ApiMonthRateException(String message) {
        super(message);
    }
}
