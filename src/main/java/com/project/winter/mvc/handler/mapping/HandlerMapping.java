package com.project.winter.mvc.handler.mapping;

import com.project.winter.mvc.handler.HandlerExecutionChain;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping {
    void init();
    HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception;
}
