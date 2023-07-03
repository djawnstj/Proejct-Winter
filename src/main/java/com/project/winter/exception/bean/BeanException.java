package com.project.winter.exception.bean;

public class BeanException extends RuntimeException {
    public BeanException() {}

    public BeanException(String message) {
        super(message);
    }
}
