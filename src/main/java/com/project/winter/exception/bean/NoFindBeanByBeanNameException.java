package com.project.winter.exception.bean;

public class NoFindBeanByBeanNameException extends BeanException {
    public NoFindBeanByBeanNameException() {}
    public NoFindBeanByBeanNameException(String message) {
        super(message);
    }
}
