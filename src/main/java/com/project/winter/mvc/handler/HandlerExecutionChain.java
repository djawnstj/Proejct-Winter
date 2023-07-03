package com.project.winter.mvc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerExecutionChain {

    public static final Logger log = LoggerFactory.getLogger(HandlerExecutionChain.class);

    private final Object handler;

    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }

    public Object getHandler() {
        return this.handler;
    }

}
