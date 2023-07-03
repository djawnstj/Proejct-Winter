package com.project.winter.beans;

import com.project.winter.annotation.Bean;
import com.project.winter.mvc.handler.mapping.BeanNameUrlHandlerMapping;
import com.project.winter.mvc.handler.mapping.HandlerMapping;
import com.project.winter.mvc.handler.mapping.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

public class BeanFactoryUtils {

    public static Map<Class<?>, Method> getBeanAnnotatedMethodInConfiguration(Class<?> configuration) {
        Map<Class<?>, Method> instantMap = new HashMap<>();

        Arrays.stream(configuration.getMethods()).forEach(method -> {
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class || instantMap.containsKey(returnType)) return;

            boolean isBeanMethod = Arrays.stream(method.getAnnotations()).anyMatch(anno -> anno.annotationType() == Bean.class);
            if (isBeanMethod) instantMap.put(returnType, method);
        });

        return instantMap;
    }

    public static List<HandlerMapping> initHandlerMappings() {
        List<HandlerMapping> handlerMappings = new ArrayList<>();

        handlerMappings.add(new RequestMappingHandlerMapping());
        handlerMappings.add(new BeanNameUrlHandlerMapping());

        handlerMappings.forEach(HandlerMapping::init);

        return handlerMappings;
    }

}
