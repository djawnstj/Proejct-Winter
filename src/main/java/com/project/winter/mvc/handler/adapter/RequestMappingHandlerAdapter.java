package com.project.winter.mvc.handler.adapter;

import com.project.winter.mvc.handler.HandlerMethod;
import com.project.winter.mvc.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod);
    }

    @Override
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        Object result = ((HandlerMethod) handler).handle(req, res);
        if (!(result instanceof String)) throw new IllegalArgumentException("Unknown return value type: " + result.getClass().getSimpleName());
        return new ModelAndView((String) result);
    }

}
