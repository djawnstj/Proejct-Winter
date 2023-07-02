package com.project.winter.exception;

public class NoFindBeanByTypeException extends BeanException {
    public NoFindBeanByTypeException() {}
    public NoFindBeanByTypeException(String message) {
        super(message);
    }
}
