package com.project.winter.resolver.exception;

import com.project.winter.annotation.Configuration;
import com.project.winter.annotation.Controller;
import com.project.winter.annotation.RequestMapping;
import com.project.winter.config.WebMvcConfigurer;
import com.project.winter.mvc.resolver.exception.HandlerExceptionResolver;
import com.project.winter.mvc.view.ModelAndView;
import com.project.winter.server.WinterServerTest;
import com.project.winter.web.HttpStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WinterServerTest
public class HandlerExceptionResolverTest {

    private static final Logger log = LoggerFactory.getLogger(HandlerExceptionResolverTest.class);

    public static class HandlerExceptionResolverTestException extends RuntimeException {

    }

    @Controller
    @RequestMapping("/test")
    public static class HandlerExceptionResolverController {

        @RequestMapping("/exception/resolver")
        public String exceptionResolverHandler() {
            log.debug("exceptionResolverHandler called.");
            throw new HandlerExceptionResolverTestException();
        }

    }

    public static class TestHandlerExceptionResolver implements HandlerExceptionResolver {

        @Override
        public ModelAndView resolveException(final HttpServletRequest req, final HttpServletResponse res, final Object handler, final Exception ex) {
            log.debug("resolveException called : {}", ex.getClass());

            try {
                if (ex instanceof HandlerExceptionResolverTestException) {
                    log.debug("HandlerExceptionResolverTestException resolve.");

                    res.setStatus(HttpStatus.NOT_FOUND.getCode());

                    return new ModelAndView("404");
                }
            } catch (Exception e) {
                log.error("fail", e);
            }
            return null;
        }
    }

    @Configuration
    public static class HandlerExceptionResolverTestConfig implements WebMvcConfigurer {

        @Override
        public void configureHandlerExceptionResolvers(final List<HandlerExceptionResolver> resolvers) {
            resolvers.add(new TestHandlerExceptionResolver());
        }
    }

    @Test
    public void test_controller_get_method_success() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/test/exception/resolver"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 404);
        assertTrue(result.body().contains("404 - Not Found"));
    }

}
