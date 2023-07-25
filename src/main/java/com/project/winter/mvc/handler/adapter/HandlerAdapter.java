package com.project.winter.mvc.handler.adapter;

import com.project.winter.mvc.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapter {
    boolean supports(Object handler);

    ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception;
}
