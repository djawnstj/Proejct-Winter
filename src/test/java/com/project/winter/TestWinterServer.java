package com.project.winter;

import com.project.winter.server.WinterServer;

public class TestWinterServer extends WinterServer {
    private static TestWinterServer instance;
    private TestWinterServer() {}
    public static TestWinterServer getInstance() {
        if (instance == null) instance = new TestWinterServer();
        return instance;
    }
}
