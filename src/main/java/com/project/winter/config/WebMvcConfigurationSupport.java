package com.project.winter.config;

import com.project.winter.mvc.handler.mapping.BeanNameUrlHandlerMapping;
import com.project.winter.mvc.handler.mapping.RequestMappingHandlerMapping;
import com.project.winter.mvc.intercpetor.MappedInterceptor;
import com.project.winter.mvc.resolver.exception.HandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

public class WebMvcConfigurationSupport implements WebMvcConfigurer {

    private final List<WebMvcConfigurer> configurers = new ArrayList<>();

    private List<MappedInterceptor> interceptors;

    private List<HandlerExceptionResolver> exceptionResolvers;

    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (!configurers.isEmpty()) {
            this.configurers.addAll(configurers);
        }
    }

    public void loadConfigurer() {

        initInterceptors();
        initHandlerExceptionResolvers();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        this.configurers.forEach(configurer -> configurer.addInterceptors(registry));
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        this.configurers.forEach(configurer -> configurer.configureHandlerExceptionResolvers(exceptionResolvers));
    }

    private void initInterceptors() {
        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        addInterceptors(interceptorRegistry);

        this.interceptors = interceptorRegistry.getInterceptors();
    }

    private void initHandlerExceptionResolvers() {
        this.exceptionResolvers = new ArrayList<>();

        configureHandlerExceptionResolvers(this.exceptionResolvers);
    }

    public MappedInterceptor[] getInterceptors() {
        return this.interceptors.toArray(new MappedInterceptor[]{});
    }

    public List<HandlerExceptionResolver> getHandlerExceptionResolvers() {
        return this.exceptionResolvers;
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
