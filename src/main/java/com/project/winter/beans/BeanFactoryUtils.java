package com.project.winter.beans;

import com.project.winter.mvc.handler.mapping.HandlerMapping;
import com.project.winter.mvc.resolver.exception.HandlerExceptionResolver;

import java.util.*;

public class BeanFactoryUtils {

    public static List<HandlerMapping> initHandlerMappings() {
        List<HandlerMapping> handlerMappings = new ArrayList<>();

        handlerMappings.add(BeanFactory.getInstance().configurationSupport.requestMappingHandlerMapping());
        handlerMappings.add(BeanFactory.getInstance().configurationSupport.beanNameUrlHandlerMapping());

        handlerMappings.forEach(HandlerMapping::init);

        return handlerMappings;
    }

    public static List<HandlerExceptionResolver> initHandlerExceptionResolvers() {
        return BeanFactory.getInstance().configurationSupport.getHandlerExceptionResolvers();
    }

}
