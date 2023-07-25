package com.project.winter.mvc.view.resolver;

import com.project.winter.mvc.view.JspView;
import com.project.winter.mvc.view.View;

public class JspViewResolver implements ViewResolver {
    @Override
    public View resolveView(String viewName) {
        return new JspView("/" + viewName + ".jsp");
    }
}
