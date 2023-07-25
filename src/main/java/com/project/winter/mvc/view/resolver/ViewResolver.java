package com.project.winter.mvc.view.resolver;

import com.project.winter.mvc.view.View;

public interface ViewResolver {
    View resolveView(String viewName);
}
