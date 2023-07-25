package com.project.winter.mvc.model;

import java.util.Map;

public class ExtendedModelMap extends ModelMap implements Model {

    @Override
    public void addAttribute(String attributeName, Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
    }

    @Override
    public void addAllAttribute(Map<String, Object> model) {
        super.addAllAttribute(model);
    }

    @Override
    public Map<String, Object> asMap() {
        return this.getModelMap();
    }

}
