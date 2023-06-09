package com.project.winter.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@WinterServerTest
class ServerRunnerTest {

    @Test
    public void test_servlet_get_method() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/get"))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(response.statusCode(), 404);
    }

    @Test
    public void test_servlet_post_method() throws Exception {

        HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/get"))
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(response.statusCode(), 404);
    }

}