package com.project.winter.server;

import com.project.winter.beans.BeanFactory;
import org.apache.catalina.*;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.logging.Level;

public class WinterServer extends Tomcat {
    private static final Logger log = LoggerFactory.getLogger(WinterServer.class);

    private static WinterServer instance;

    private int port = 8080;

    protected WinterServer() {}

    public static WinterServer getInstance() {
        if (instance == null) instance = new WinterServer();
        return instance;
    }

    public void addLifecycleListener(LifecycleListener listener) {
        getServer().addLifecycleListener(listener);
    }

    private static void offTomcatLogger() {
        java.util.logging.Logger tomcatCoreLogger = java.util.logging.Logger.getLogger("org.apache");

        tomcatCoreLogger.setLevel(Level.OFF);
    }

    public void startServer() {
        setTomcat();

        try {
            start();
            getServer().await();
        } catch (LifecycleException e) {
            log.error("Failed to start Server", e);
        }
    }

    private void setTomcat() {
        addLifecycleListener((event -> {
            if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) BeanFactory.initInstance();
        }));

        offTomcatLogger();

        log.info("Server port: {}", port);
        setPort(port);

        setServerContext();
    }

    private void setServerContext() {
        String resourcesPath = "src\\main\\resources";

        File resourceTempFile = new File(resourcesPath);
        String absolutePath = resourceTempFile.getAbsolutePath();
        log.debug("resource path: {}", absolutePath);

        Context context = addWebapp("/", absolutePath);

        String currentClassPath = getClassesPath();
        log.debug("current class path: {}", currentClassPath);

        StandardRoot resources = new StandardRoot(context);
        resources.addPostResources(new DirResourceSet(resources, "/WEB-INF/classes", currentClassPath, "/"));

        context.setResources(resources);
    }


    private String getClassesPath() {
        Class<? extends WinterServer> clazz = getClass();
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();

        CodeSource codeSource = protectionDomain.getCodeSource();

        try {
            if (codeSource == null) throw new IllegalStateException("Can't getCodeSource.");
            URL location = codeSource.getLocation();
            File file = new File(location.toURI());

            String classesPath = file.getAbsolutePath();
            log.debug("found class path: {}", classesPath);

            return classesPath;
        } catch (URISyntaxException | IllegalStateException e) {
            log.error("Failed to get classes path", e);
        }

        return null;
    }

}
