package com.project.winter.config;

import com.project.winter.mvc.intercpetor.HandlerInterceptor;
import com.project.winter.mvc.intercpetor.MappedInterceptor;

import java.util.ArrayList;
import java.util.List;

public class InterceptorRegistry {

    private final List<InterceptorRegistration> registrations = new ArrayList<>();

    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        this.registrations.add(registration);
        return registration;
    }

    protected List<MappedInterceptor> getInterceptors() {
        return this.registrations
                .stream()
                .map(InterceptorRegistration::getInterceptor)
                .toList();
    }

}
