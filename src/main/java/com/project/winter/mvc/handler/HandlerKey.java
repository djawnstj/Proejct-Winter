package com.project.winter.mvc.handler;

import com.project.winter.web.HttpMethod;

import java.util.Objects;

public class HandlerKey {

    private String path;

    private HttpMethod method;

    public HandlerKey(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HandlerKey that)) return false;
        return Objects.equals(path, that.path) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }
}
