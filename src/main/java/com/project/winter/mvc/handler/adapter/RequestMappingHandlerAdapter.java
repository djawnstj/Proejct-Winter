package com.project.winter.mvc.handler.adapter;

import com.project.winter.mvc.handler.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod);
    }

    @Override
    public String handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        return ((HandlerMethod) handler).handle(req, res);
    }
}
