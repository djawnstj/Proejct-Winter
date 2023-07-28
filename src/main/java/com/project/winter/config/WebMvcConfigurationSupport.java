package com.project.winter.config;

import com.project.winter.mvc.handler.mapping.BeanNameUrlHandlerMapping;
import com.project.winter.mvc.handler.mapping.HandlerMapping;
import com.project.winter.mvc.handler.mapping.RequestMappingHandlerMapping;
import com.project.winter.mvc.intercpetor.MappedInterceptor;

import java.util.ArrayList;
import java.util.List;

public class WebMvcConfigurationSupport implements WebMvcConfigurer {

    private final List<WebMvcConfigurer> configurers = new ArrayList<>();

    private List<MappedInterceptor> interceptors;

    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (!configurers.isEmpty()) {
            this.configurers.addAll(configurers);
        }
    }

    public void loadConfigurer() {

        initInterceptors();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        this.configurers.forEach(configurer -> configurer.addInterceptors(registry));
    }

    private void initInterceptors() {
        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        addInterceptors(interceptorRegistry);

        this.interceptors = interceptorRegistry.getInterceptors();
    }

    public MappedInterceptor[] getInterceptors() {
        return this.interceptors.toArray(new MappedInterceptor[]{});
    }

    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        final RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();

        mapping.setInterceptors(getInterceptors());

        return mapping;
    }

    public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping() {
        final BeanNameUrlHandlerMapping mapping = new BeanNameUrlHandlerMapping();

        mapping.setInterceptors(getInterceptors());

        return mapping;
    }

}
