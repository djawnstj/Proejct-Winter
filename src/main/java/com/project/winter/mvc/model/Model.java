package com.project.winter.mvc.model;

import java.util.Map;

public interface Model {
    void addAttribute(String attributeName, Object attributeValue);

    Map<String, Object> asMap();
}
