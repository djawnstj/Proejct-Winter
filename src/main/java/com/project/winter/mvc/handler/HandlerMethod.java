package com.project.winter.mvc.handler;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private Object[] initParameters(HttpServletRequest req, HttpServletResponse res) throws Exception {
        List<Object> parameterList = new ArrayList<>();

        Arrays.stream(this.method.getParameters()).forEach(parameter -> {
            Class<?> parameterType = parameter.getType();
            if (parameterType == ServletRequest.class) parameterList.add(req);
            else if (parameter.getType() == ServletResponse.class) parameterList.add(res);
            else {
                // TODO 서블릿 요청/응답을 제외한 나머지 파라미터에 대한 처리 필요
            }
        });

        return parameterList.toArray();
    }

    public Object handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        this.parameters = initParameters(req, res);

        return this.method.invoke(bean, parameters);
    }

}
