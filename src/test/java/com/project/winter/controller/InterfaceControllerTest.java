package com.project.winter.controller;

import com.project.winter.annotation.Bean;
import com.project.winter.annotation.Configuration;
import com.project.winter.mvc.controller.Controller;
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

import static org.junit.jupiter.api.Assertions.*;

@WinterServerTest
public class InterfaceControllerTest {
    private final static Logger log = LoggerFactory.getLogger(InterfaceControllerTest.class);

    public static class TestInterfaceController implements Controller {
        @Override
        public String handleRequest(HttpServletRequest req, HttpServletResponse res) {
            log.debug("TestInterfaceController handleRequest called.");
            return "get";
        }
    }

    @Configuration
    public static class InterfaceControllerConfiguration {
        @Bean(name = "/interface")
        public TestInterfaceController testInterfaceController() {
            return new TestInterfaceController();
        }
    }

    @Test
    public void test_controller_get_method_success() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interface"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 200);
    }

    @Test
    public void test_controller_get_method_using_post_method_failure() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/interface"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 404);
    }

}
