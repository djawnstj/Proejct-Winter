package com.project.winter.mvc.handler.mapping;

import com.project.winter.beans.BeanFactory;
import com.project.winter.beans.BeanInfo;
import com.project.winter.mvc.controller.Controller;
import com.project.winter.mvc.handler.HandlerKey;
import com.project.winter.mvc.handler.HandlerMethod;
import com.project.winter.web.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

public class BeanNameUrlHandlerMapping extends AbstractHandlerMapping {

    @Override
    public void init() {
        Map<BeanInfo, Controller> controllerMap = BeanFactory.getBeans(Controller.class);

        controllerMap.forEach((key, controller) -> {
            try {
                Method handlerRequest = controller.getClass().getMethod("handleRequest", HttpServletRequest.class, HttpServletResponse.class);

                HandlerMethod handlerMethod = new HandlerMethod(controller, handlerRequest);

                HandlerKey handlerKey = new HandlerKey(key.getBeanName(), HttpMethod.GET);

                handlerMethods.put(handlerKey, handlerMethod);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
