package com.project.winter.beans;

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
        return Objects.equals(clazz, beanInfo.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, clazz);
    }
}
