package com.project.winter.server;

import org.apache.catalina.Lifecycle;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.CountDownLatch;

public class TestServerExtension implements BeforeAllCallback {

    private static CountDownLatch serverStartedLatch = new CountDownLatch(1);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ServerRunner.setWebappDirLocation("webapps/test/");
        ServerRunner.addLifecycleListener((event) -> {
            if (event.getType().equals(Lifecycle.AFTER_START_EVENT)) serverStartedLatch.countDown();
        });
        ServerRunner.startServer();
        serverStartedLatch.await();
    }
}
