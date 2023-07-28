package com.project.winter.mvc.intercpetor;

import com.project.winter.mvc.view.ModelAndView;
import com.project.winter.web.util.PathPattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MappedInterceptor implements HandlerInterceptor {

    private final HandlerInterceptor interceptor;

    private final PathAdapter[] includePaths;

    private final PathAdapter[] excludePaths;

    public MappedInterceptor(HandlerInterceptor interceptor, String[] includePaths, String[] excludePaths) {
        this.interceptor = interceptor;
        this.includePaths = PathAdapter.initPathAdapters(includePaths);
        this.excludePaths = PathAdapter.initPathAdapters(excludePaths);
    }

    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();

        for (PathAdapter adapter : this.excludePaths) {
            if (adapter.match(uri)) return false;
        }

        if (this.includePaths.length == 0) return true;

        for (PathAdapter adapter : this.includePaths) {
            if (adapter.match(uri)) return true;
        }

        return false;
    }

    public HandlerInterceptor getInterceptor() {
        return this.interceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.interceptor.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        this.interceptor.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.interceptor.afterCompletion(request, response, handler, ex);
    }

    private static class PathAdapter {

        private final String path;

        private final List<PathPattern> patterns;

        private PathAdapter(String path) {
            this.path = path;
            this.patterns = new ArrayList<>();

            final String[] patterns = PathPattern.getPatterns(path);

            for (int i = 0; i < patterns.length; i++) {
                this.patterns.add(new PathPattern(patterns[i], i));
            }

            Collections.sort(this.patterns);
        }

        boolean match(String requestPath) {
            String[] requestPatterns = PathPattern.getPatterns(requestPath);

            for (PathPattern pattern : this.patterns) {
                int order = pattern.getOrder();

                if (requestPatterns.length < order) break;

                boolean isMatch = pattern.match(requestPatterns[order]);

                if (!isMatch) return false;
            }

            return true;
        }

        public static PathAdapter[] initPathAdapters(String[] paths) {
            return Arrays.stream(paths)
                    .map(PathAdapter::new)
                    .toArray(PathAdapter[]::new);
        }

    }

}
