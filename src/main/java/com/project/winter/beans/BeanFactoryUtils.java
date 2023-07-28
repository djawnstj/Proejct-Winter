package com.project.winter.beans;

import com.project.winter.mvc.handler.mapping.HandlerMapping;

import java.util.*;

public class BeanFactoryUtils {

    public static List<HandlerMapping> initHandlerMappings() {
        List<HandlerMapping> handlerMappings = new ArrayList<>();

        handlerMappings.add(BeanFactory.getInstance().configurationSupport.requestMappingHandlerMapping());
        handlerMappings.add(BeanFactory.getInstance().configurationSupport.beanNameUrlHandlerMapping());

        handlerMappings.forEach(HandlerMapping::init);

        return handlerMappings;
    }

}
