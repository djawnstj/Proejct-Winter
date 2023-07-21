package com.project.winter.mvc.handler.mapping;

import com.project.winter.annotation.Controller;
import com.project.winter.annotation.RequestMapping;
import com.project.winter.beans.BeanFactory;
import com.project.winter.beans.BeanInfo;
import com.project.winter.mvc.handler.HandlerKey;
import com.project.winter.mvc.handler.HandlerMethod;
import com.project.winter.web.HttpMethod;

import java.lang.reflect.Method;
import java.util.*;

public class RequestMappingHandlerMapping extends AbstractHandlerMapping {

    @Override
    public void init() {
        Map<BeanInfo, Object> controllers = BeanFactory.getInstance().getAnnotatedBeans(Controller.class);

        controllers.forEach((key, controller) -> {
            Class<?> clazz = controller.getClass();
            Method[] controllerMethods = clazz.getDeclaredMethods();

            StringBuilder parentPath = new StringBuilder();
            Set<HttpMethod> parentHttpMethods = new HashSet<>();

            boolean classHasRequestMapping = clazz.isAnnotationPresent(RequestMapping.class);

            if (classHasRequestMapping) {
                RequestMapping requestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);

                setRequestMappingInfo(requestMapping, parentPath, parentHttpMethods);
            }

            Arrays.stream(controllerMethods).forEach(method -> {

                StringBuilder path = new StringBuilder(parentPath.toString());
                Set<HttpMethod> httpMethods = new HashSet<>(parentHttpMethods);

                boolean methodHasRequestMapping = method.isAnnotationPresent(RequestMapping.class);

                if (methodHasRequestMapping) {
                    RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);

                    setRequestMappingInfo(requestMapping, path, httpMethods);
                }

                if (httpMethods.isEmpty()) httpMethods.addAll(List.of(HttpMethod.values()));

                httpMethods.forEach(it -> {
                    HandlerKey handlerKey = new HandlerKey(path.toString(), it);
                    HandlerMethod handlerMethod = new HandlerMethod(controller, method);

                    handlerMethods.put(handlerKey, handlerMethod);
                });
            });
        });
    }

    private void setRequestMappingInfo(RequestMapping requestMapping, StringBuilder path, Set<HttpMethod> httpMethods) {
        String tempPath = requestMapping.value();

        if (!tempPath.startsWith("/")) path.append("/");
        path.append(tempPath);

        httpMethods.addAll(List.of(requestMapping.method()));
    }

}
