package com.project.winter.mvc.handler;

import com.project.winter.mvc.intercpetor.HandlerInterceptor;
import com.project.winter.mvc.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HandlerExecutionChain {

    public static final Logger log = LoggerFactory.getLogger(HandlerExecutionChain.class);

    private final Object handler;

    private final List<HandlerInterceptor> interceptorList = new ArrayList<>();

    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler) {
        this(handler, (new HandlerInterceptor[] {}));
    }

    public HandlerExecutionChain(Object handler, HandlerInterceptor... interceptors) {
        this(handler, Arrays.asList(interceptors));
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        if (handler instanceof HandlerExecutionChain) {
            final HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        }
        else this.handler = handler;

        this.interceptorList.addAll(interceptorList);
    }

    public Object getHandler() {
        return this.handler;
    }

    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    public void addInterceptor(int index, HandlerInterceptor interceptor) {
        this.interceptorList.add(index, interceptor);
    }

    public void addInterceptors(HandlerInterceptor... interceptors) {
        this.interceptorList.addAll(Arrays.asList(interceptors));
    }

    public List<HandlerInterceptor> getInterceptorList() {
        return (!this.interceptorList.isEmpty() ? Collections.unmodifiableList(this.interceptorList) :
                Collections.emptyList());
    }

    public boolean applyPreHandle(HttpServletRequest req, HttpServletResponse res) throws Exception {
        final int size = this.interceptorList.size();

        for (int i = 0; i < size; i++) {
   			HandlerInterceptor interceptor = this.interceptorList.get(i);
   			if (!interceptor.preHandle(req, res, this.handler)) {
   				triggerAfterCompletion(req, res, null);
   				return false;
   			}
   			this.interceptorIndex = i;
   		}

   		return true;
   	}

   	public void applyPostHandle(HttpServletRequest req, HttpServletResponse res, ModelAndView mv) throws Exception {
   		for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
   			HandlerInterceptor interceptor = this.interceptorList.get(i);
   			interceptor.postHandle(req, res, this.handler, mv);
   		}
   	}

   	public void triggerAfterCompletion(HttpServletRequest req, HttpServletResponse res, Exception ex) {
   		for (int i = this.interceptorIndex; i >= 0; i--) {
   			HandlerInterceptor interceptor = this.interceptorList.get(i);
   			try {
   				interceptor.afterCompletion(req, res, this.handler, ex);
   			}
   			catch (Throwable ex2) {
   				log.error("afterCompletion threw exception", ex2);
   			}
   		}
   	}

}
