package com.project.winter.server;

import com.project.winter.beans.BeanFactory;
import org.apache.catalina.*;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class ServerRunner {

    private static Tomcat tomcat;
    private static int port;
    private static String webappDirLocation;
    private static Thread serverThread;

    static {
        webappDirLocation = "webapps/";

        tomcat = new Tomcat();
        port = 8080;

        serverThread = new Thread(() -> {
            try {
                tomcat.setPort(port);
                tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

                tomcat.start();
                tomcat.getServer().await();
            } catch (LifecycleException e) {
                e.printStackTrace();
            }

        });
    }

    public static void setPort(int port) {
        ServerRunner.port = port;
    }

    public static void setWebappDirLocation(String webappDirLocation) {
        ServerRunner.webappDirLocation = webappDirLocation;
    }

    public static void addLifecycleListener(LifecycleListener listener) {
        tomcat.getServer().addLifecycleListener(listener);
    }

    public static void startServer() {
        serverThread.start();
        BeanFactory.initialize();
    }

}
