package com.project.winter.mvc.handler.mapping;

import com.project.winter.mvc.handler.HandlerExecutionChain;
import com.project.winter.mvc.handler.HandlerKey;
import com.project.winter.mvc.intercpetor.HandlerInterceptor;
import com.project.winter.mvc.intercpetor.MappedInterceptor;
import com.project.winter.web.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public abstract class AbstractHandlerMapping implements HandlerMapping {

    protected Map<HandlerKey, Object> handlerMethods = new LinkedHashMap<>();

    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    public void setInterceptors(HandlerInterceptor... interceptors) {
        this.interceptors.addAll(Arrays.asList(interceptors));
    }

    private Object getHandlerInternal(HttpServletRequest req) throws Exception {
        HandlerKey handlerKey = new HandlerKey(req.getRequestURI(), HttpMethod.valueOf(req.getMethod()));

        return handlerMethods.get(handlerKey);
    }

    public HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception {
        Object handler = getHandlerInternal(req);

        if (handler == null) return null;

        final HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, req);

        return executionChain;
    }

    private HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest req) {
   		HandlerExecutionChain chain = (handler instanceof HandlerExecutionChain ? (HandlerExecutionChain) handler : new HandlerExecutionChain(handler));

   		for (HandlerInterceptor interceptor : this.interceptors) {
   			if (interceptor instanceof MappedInterceptor) {
   				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
   				if (mappedInterceptor.matches(req)) {
   					chain.addInterceptor(mappedInterceptor.getInterceptor());
   				}
   			}
   			else {
   				chain.addInterceptor(interceptor);
   			}
   		}
   		return chain;
   	}

}
