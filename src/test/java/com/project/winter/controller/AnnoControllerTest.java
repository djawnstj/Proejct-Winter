package com.project.winter.controller;

import com.project.winter.annotation.Controller;
import com.project.winter.annotation.RequestMapping;
import com.project.winter.server.WinterServerTest;
import com.project.winter.web.HttpMethod;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@WinterServerTest
public class AnnoControllerTest {

    private final static Logger log = LoggerFactory.getLogger(AnnoControllerTest.class);

    @Controller
    @RequestMapping(value = "/anno", method = {HttpMethod.GET})
    public static class TestAnnoController {

        @RequestMapping("/get")
        public String getTest() {
            log.debug("TestAnnoController getTest called.");
            return "get";
        }

        @RequestMapping(value = "/post", method = {HttpMethod.POST})
        public String postTest() {
            log.debug("TestAnnoController postTest called.");
            return "post";
        }

    }

    @Test
    public void test_controller_get_method_success() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/anno/get"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 200);
    }

    @Test
    public void test_controller_get_method_failure() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/anno/error"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 404);
    }

    @Test
    public void test_controller_get_method_using_post_method_failure() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/anno/get"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 404);
    }

    @Test
    public void test_controller_post_method_success() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/anno/post"))
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 200);
    }

    @Test
    public void test_controller_post_method_using_get_method_success() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/anno/post"))
                .build();

        HttpResponse<String> result = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(result.statusCode(), 200);
    }

}
