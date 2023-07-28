package com.project.winter.config;

import com.project.winter.mvc.intercpetor.HandlerInterceptor;
import com.project.winter.mvc.intercpetor.MappedInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptorRegistration {

    private final HandlerInterceptor interceptor;

   	private List<String> includePaths;

   	private List<String> excludePaths;

    public InterceptorRegistration(HandlerInterceptor interceptor) {
        if (interceptor == null) throw new IllegalArgumentException("Interceptor is required");
        this.interceptor = interceptor;
    }

    public InterceptorRegistration addPathPatterns(String... paths) {
        addPathPatterns(Arrays.asList(paths));

        return this;
    }

    public InterceptorRegistration addPathPatterns(List<String> paths) {
        this.includePaths = (this.includePaths != null) ? this.includePaths : new ArrayList<>(paths.size());
        this.includePaths.addAll(paths);

        return this;
    }

    public InterceptorRegistration excludePathPatterns(String... paths) {
        excludePathPatterns(Arrays.asList(paths));

        return this;
    }

    public InterceptorRegistration excludePathPatterns(List<String> paths) {
        this.excludePaths = (this.excludePaths != null) ? this.excludePaths : new ArrayList<>(paths.size());
        this.excludePaths.addAll(paths);

        return this;
    }

    protected MappedInterceptor getInterceptor() {
        String[] includePathsEmptyArray = {};
        String[] excludePathsEmptyArray = {};
        String[] includePathsArray = (this.includePaths == null) ? includePathsEmptyArray : this.includePaths.toArray(includePathsEmptyArray);
        String[] excludePathsArray = (this.excludePaths == null) ? excludePathsEmptyArray : this.excludePaths.toArray(excludePathsEmptyArray);

        return new MappedInterceptor(this.interceptor, includePathsArray, excludePathsArray);
    }

}
