package com.project.winter.mvc.view;

import com.project.winter.mvc.model.ExtendedModelMap;
import com.project.winter.mvc.model.ModelMap;
import com.project.winter.web.HttpStatus;

import java.util.Map;

public class ModelAndViewContainer {

    Object view;

    ModelMap model = new ExtendedModelMap();

    HttpStatus status;

    public void setViewName(String viewName) {
        this.view = viewName;
    }

    public String getViewName() {
   		return (this.view instanceof String ? (String) this.view : null);
   	}

    public Object getView() {
        return this.view;
    }

    public void setView(Object view) {
        this.view = view;
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        this.model.addAttribute(attributeName, attributeValue);
    }

    public void addAllAttribute(Map<String, Object> model) {
        this.model.addAllAttribute(model);
    }

    public ModelMap getModel() {
        return model;
    }

    public Map<String, Object> getModelMap() {
        return model.getModelMap();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
