package com.project.winter.mvc.handler;

import com.project.winter.mvc.model.Model;
import com.project.winter.mvc.model.ModelMap;
import com.project.winter.mvc.view.ModelAndViewContainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandlerMethod {

    private final Object bean;

    private final Method method;

    private Object[] parameters;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    private Object[] initParameters(HttpServletRequest req, HttpServletResponse res, ModelAndViewContainer mavContainer) throws Exception {
        List<Object> parameterList = new ArrayList<>();

        Arrays.stream(this.method.getParameters()).forEach(parameter -> {
            Class<?> parameterType = parameter.getType();
            if (ServletRequest.class.isAssignableFrom(parameterType)) parameterList.add(req);
            else if (ServletResponse.class.isAssignableFrom(parameterType)) parameterList.add(res);
            else if (Model.class.isAssignableFrom(parameterType)) parameterList.add(mavContainer.getModel());
            else if (ModelMap.class.isAssignableFrom(parameterType)) parameterList.add(mavContainer.getModel());
            else {
                // TODO 서블릿 요청/응답을 제외한 나머지 파라미터에 대한 처리 필요
            }
        });

        return parameterList.toArray();
    }

    public void handle(HttpServletRequest req, HttpServletResponse res, ModelAndViewContainer mavContainer) throws Exception {
        this.parameters = initParameters(req, res, mavContainer);

        try {
            Object view = this.method.invoke(bean, parameters);

            mavContainer.setView(view);

            req.getAttributeNames().asIterator().forEachRemaining(name -> {
                Object value = req.getAttribute(name);
                mavContainer.addAttribute(name, value);
            });
        } catch (InvocationTargetException e) {
            Throwable originalException = e.getTargetException();
            if (originalException instanceof RuntimeException) throw (RuntimeException) originalException;
            else if (originalException instanceof Exception) throw (Exception) originalException;
        }
    }

}
