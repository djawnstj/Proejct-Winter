package com.project.winter.exception;

public class NoMatchByBeanNameException extends BeanException {
    public NoMatchByBeanNameException() {}
    public NoMatchByBeanNameException(String message) {
        super(message);
    }
}
