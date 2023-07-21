package com.project.winter;

import com.project.winter.server.WinterServer;

public class WinterApplication {
    public static void main(String[] args) {
        WinterServer.getInstance().startServer();
    }
}
