package com.project.winter.mvc.handler.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
    boolean supports(Object handler);

    String handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception;
}
