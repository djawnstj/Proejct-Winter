package com.project.winter.beans;

import com.project.winter.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

}
