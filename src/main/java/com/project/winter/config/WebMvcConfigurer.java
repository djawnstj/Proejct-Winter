package com.project.winter.config;

import com.project.winter.mvc.resolver.exception.HandlerExceptionResolver;

import java.util.List;

public interface WebMvcConfigurer {

    default void addInterceptors(InterceptorRegistry registry) {}

    default void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {}

}
