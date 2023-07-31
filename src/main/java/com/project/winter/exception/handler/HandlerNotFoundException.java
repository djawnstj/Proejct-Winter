package com.project.winter.exception.handler;

import com.project.winter.mvc.view.ModelAndView;

public class HandlerNotFoundException extends HandlerException {
    public HandlerNotFoundException() {}

    public HandlerNotFoundException(String message) {
        super(message);
    }

    public ModelAndView getModelAndView() {
        return new ModelAndView("404");
    }
}
