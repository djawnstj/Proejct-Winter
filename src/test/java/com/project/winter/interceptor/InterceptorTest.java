package com.project.winter.interceptor;

import com.project.winter.annotation.Bean;
import com.project.winter.annotation.Configuration;
import com.project.winter.annotation.Controller;
import com.project.winter.annotation.RequestMapping;
import com.project.winter.config.InterceptorRegistry;
import com.project.winter.config.WebMvcConfigurer;
import com.project.winter.mvc.intercpetor.HandlerInterceptor;
import com.project.winter.mvc.view.ModelAndView;
import com.project.winter.server.WinterServerTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WinterServerTest
public class InterceptorTest {

    public static class TestInterceptor implements HandlerInterceptor {
        private final Logger log = LoggerFactory.getLogger(TestInterceptor.class);

        @Override
        public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
            log.debug("preHandle called: {}", request.getRequestURI());
            return true;
        }

        @Override
        public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
            log.debug("postHandle called: {}", request.getRequestURI());
        }

        @Override
        public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final Exception ex) throws Exception {
            log.debug("afterCompletion called: {}", request.getRequestURI());
        }
    }

    @Controller
    @RequestMapping("/interceptor/anno")
    public static class InterceptorTestAnnotationController {
        @RequestMapping("/include")
        public String include() {
            return "test";
        }

        @RequestMapping("/exclude")
        public String exclude() {
            return "test";
        }

        @RequestMapping("/exception")
        public String exception() {
            throw new IllegalStateException("throw exception");
        }
    }

    public static class InterceptorTestBeanIncludeController implements com.project.winter.mvc.controller.Controller {
        @Override
        public ModelAndView handleRequest(final HttpServletRequest req, final HttpServletResponse res) {
            return new ModelAndView("test");
        }
    }

    public static class InterceptorTestBeanExcludeController implements com.project.winter.mvc.controller.Controller {
        @Override
        public ModelAndView handleRequest(final HttpServletRequest req, final HttpServletResponse res) {
            return new ModelAndView("test");
        }
    }

    public static class InterceptorTestBeanExceptionController implements com.project.winter.mvc.controller.Controller {
        @Override
        public ModelAndView handleRequest(final HttpServletRequest req, final HttpServletResponse res) {
            throw new IllegalStateException("throw exception");
        }
    }

    @Configuration
    public static class InterceptorConfig implements WebMvcConfigurer {
        @Bean(name = "/interceptor/bean/include")
        public InterceptorTestBeanIncludeController interceptorTestBeanIncludeController() {
            return new InterceptorTestBeanIncludeController();
        }

        @Bean(name = "/interceptor/bean/exclude")
        public InterceptorTestBeanExcludeController interceptorTestBeanExcludeController() {
            return new InterceptorTestBeanExcludeController();
        }

        @Bean(name = "/interceptor/bean/exception")
        public InterceptorTestBeanExceptionController interceptorTestBeanExceptionController() {
            return new InterceptorTestBeanExceptionController();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new TestInterceptor())
                    .addPathPatterns("/interceptor/*")
                    .excludePathPatterns("/interceptor/*/exclude");
        }
    }

    @Test
    public void test_interceptor_annotation_controller_include() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/anno/include"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void test_interceptor_annotation_controller_exclude() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/anno/exclude"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void test_interceptor_annotation_controller_exception() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/anno/exception"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void test_interceptor_bean_controller_include() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/bean/include"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void test_interceptor_bean_controller_exclude() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/bean/exclude"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void test_interceptor_bean_controller_exception() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interceptor/bean/exception"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

}
