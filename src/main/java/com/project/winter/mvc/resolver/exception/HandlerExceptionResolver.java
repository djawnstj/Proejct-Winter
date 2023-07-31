package com.project.winter.mvc.resolver.exception;

import com.project.winter.mvc.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerExceptionResolver {
    ModelAndView resolveException(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex);
}
