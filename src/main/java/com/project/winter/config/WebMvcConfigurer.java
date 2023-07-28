package com.project.winter.config;

public interface WebMvcConfigurer {

    default void addInterceptors(InterceptorRegistry registry) {}

}
