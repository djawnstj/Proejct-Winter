package com.project.winter.mvc.handler.mapping;

import com.project.winter.beans.BeanFactory;
import com.project.winter.beans.BeanInfo;
import com.project.winter.mvc.controller.Controller;
import com.project.winter.mvc.handler.HandlerKey;
import com.project.winter.web.HttpMethod;

import java.util.Map;

public class BeanNameUrlHandlerMapping extends AbstractHandlerMapping {

    @Override
    public void init() {
        Map<BeanInfo, Controller> controllerMap = BeanFactory.getInstance().getBeans(Controller.class);

        controllerMap.forEach((key, controller) -> {
            String beanName = key.getBeanName();
            if (!beanName.startsWith("/")) beanName = "/" + beanName;

            HandlerKey handlerKey = new HandlerKey(beanName, HttpMethod.GET);
            handlerMethods.put(handlerKey, controller);
        });
    }

}
