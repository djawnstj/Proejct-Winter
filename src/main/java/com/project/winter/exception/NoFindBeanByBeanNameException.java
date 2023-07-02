package com.project.winter.exception;

public class NoFindBeanByBeanNameException extends BeanException {
    public NoFindBeanByBeanNameException() {}
    public NoFindBeanByBeanNameException(String message) {
        super(message);
    }
}
