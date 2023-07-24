package com.project.winter.mvc.view;

import com.project.winter.mvc.model.ModelMap;

import java.util.Map;

public class ModelAndView {
    private Object view;
    private ModelMap model;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.view = viewName;
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.view = viewName;
        this.getModel().addAllAttribute(model);
    }

    public Object getView() {
        return view;
    }

    public String getViewName() {
        return (this.view instanceof String) ? (String) this.view : null;
    }

    public ModelMap getModel() {
        if (this.model == null) this.model = new ModelMap();
        return model;
    }

    public Map<String, Object> getModelInternal() {
        return this.getModel().getModelMap();
    }

}
