package com.project.winter.mvc.handler.mapping;

import com.project.winter.mvc.handler.HandlerExecutionChain;
import com.project.winter.mvc.handler.HandlerKey;
import com.project.winter.web.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractHandlerMapping implements HandlerMapping {

    protected Map<HandlerKey, Object> handlerMethods = new LinkedHashMap<>();

    private Object getHandlerInternal(HttpServletRequest req) throws Exception {
        HandlerKey handlerKey = new HandlerKey(req.getRequestURI(), HttpMethod.valueOf(req.getMethod()));

        return handlerMethods.get(handlerKey);
    }

    public HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception {
        Object handler = getHandlerInternal(req);

        if (handler == null) return null;

        return new HandlerExecutionChain(handler);
    }

}
