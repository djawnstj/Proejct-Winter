package com.project.winter.mvc.handler.adapter;

import com.project.winter.mvc.handler.HandlerMethod;
import com.project.winter.mvc.view.ModelAndView;
import com.project.winter.mvc.view.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod);
    }

    @Override
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        ((HandlerMethod) handler).handle(req, res, mavContainer);
        Object view = mavContainer.getView();
        if (!(view instanceof String)) throw new IllegalArgumentException("Unknown return value type: " + view.getClass().getSimpleName());

        return getModelAndView(mavContainer);
    }

    public ModelAndView getModelAndView(ModelAndViewContainer mavContainer) {
        return new ModelAndView(mavContainer.getViewName(), mavContainer.getModelMap());
    }

}
