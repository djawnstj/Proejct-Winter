package com.project.winter.beans;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class BeanInfo {
    private final String beanName;
    private final Class<?> clazz;

    public BeanInfo(String beanName, Class<?> clazz) {
        this.beanName = beanName;
        this.clazz = clazz;
    }

    public boolean isCorrespondName(String beanName) {
        return this.beanName.equals(beanName);
    }

    public boolean sameType(Class<?> type) {
        return this.clazz == type;
    }

    public boolean isAssignableFrom(Class<?> type) {
        return type.isAssignableFrom(this.clazz);
    }

    public boolean isAnnotated(Class<? extends Annotation> annotation) {
        return clazz.isAnnotationPresent(annotation);
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanInfo beanInfo = (BeanInfo) o;
        return beanInfo.clazz.isAssignableFrom(clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, clazz);
    }
}
