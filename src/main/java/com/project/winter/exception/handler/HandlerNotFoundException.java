package com.project.winter.exception.handler;

public class HandlerNotFoundException extends HandlerException {
    public HandlerNotFoundException() {}

    public HandlerNotFoundException(String message) {
        super(message);
    }
}
