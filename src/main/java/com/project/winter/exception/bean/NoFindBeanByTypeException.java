package com.project.winter.exception.bean;

public class NoFindBeanByTypeException extends BeanException {
    public NoFindBeanByTypeException() {}
    public NoFindBeanByTypeException(String message) {
        super(message);
    }
}
