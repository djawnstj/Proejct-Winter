package com.project.winter.mvc.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelMap {

    private final Map<String, Object> modelMap = new LinkedHashMap<>();

    public ModelMap() {
    }

    public ModelMap(Map<String, Object> model) {
        this.modelMap.putAll(model);
    }

    public ModelMap(String attributeName, Object attributeValue) {
        this.addAttribute(attributeName, attributeValue);
    }

    public void addAttribute(String attributeName, Object attributeValue) {
        this.modelMap.put(attributeName, attributeValue);
    }

    public void addAllAttribute(Map<String, Object> model) {
        this.modelMap.putAll(model);
    }

    public Map<String, Object> getModelMap() {
        return this.modelMap;
    }

}
