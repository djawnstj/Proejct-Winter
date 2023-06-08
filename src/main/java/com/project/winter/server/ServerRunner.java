package com.project.winter.server;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class ServerRunner {

    private static Tomcat tomcat;
    private static int port;
    private static String webappDirLocation;

    static {
        webappDirLocation = "webapps/";

        tomcat = new Tomcat();
        port = 8080;

        tomcat.setPort(port);
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
    }

    public static void start() {

        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

    }

}
