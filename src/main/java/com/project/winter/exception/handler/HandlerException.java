package com.project.winter.exception.handler;

public class HandlerException extends RuntimeException {
    public HandlerException() {}
    public HandlerException(String message) {
        super(message);
    }
}
