package com.project.winter;

import com.project.winter.beans.BeanFactoryUtils;
import com.project.winter.exception.handler.HandlerNotFoundException;
import com.project.winter.mvc.handler.HandlerExecutionChain;
import com.project.winter.mvc.handler.adapter.HandlerAdapter;
import com.project.winter.mvc.handler.adapter.RequestMappingHandlerAdapter;
import com.project.winter.mvc.handler.adapter.SimpleControllerHandlerAdapter;
import com.project.winter.mvc.handler.mapping.HandlerMapping;
import com.project.winter.mvc.resolver.exception.HandlerExceptionResolver;
import com.project.winter.mvc.view.ModelAndView;
import com.project.winter.mvc.view.View;
import com.project.winter.mvc.view.resolver.JspViewResolver;
import com.project.winter.mvc.view.resolver.ViewResolver;
import com.project.winter.web.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private List<HandlerMapping> handlerMappings;

    private List<HandlerAdapter> handlerAdapters;

    private List<ViewResolver> viewResolvers;

    private List<HandlerExceptionResolver> handlerExceptionResolvers;

    @Override
    public void init() throws ServletException {
        log.info("DispatcherServlet init() called.");

        initHandlerMappings();
        initHandlerAdapters();
        initViewResolvers();
        initHandlerExceptionResolvers();
    }

    private void initHandlerMappings() {
        this.handlerMappings = BeanFactoryUtils.initHandlerMappings();
    }

    private void initHandlerAdapters() {
        this.handlerAdapters = new ArrayList<>();
        this.handlerAdapters.add(new RequestMappingHandlerAdapter());
        this.handlerAdapters.add(new SimpleControllerHandlerAdapter());
    }

    private void initViewResolvers() {
        this.viewResolvers = new ArrayList<>();
        this.viewResolvers.add(new JspViewResolver());
    }

    private void initHandlerExceptionResolvers() {
        this.handlerExceptionResolvers = BeanFactoryUtils.initHandlerExceptionResolvers();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("DispatcherServlet service() called.");

        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse res) {

        HandlerExecutionChain mappedHandler = null;
        Exception dispatchException = null;

        try {
            ModelAndView mv = null;

            try {
                mappedHandler = getHandler(req);

                if (mappedHandler == null) {
                    noHandlerFound(req, res);
                    return;
                }

                HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

                if (!mappedHandler.applyPreHandle(req, res)) {
                    return ;
                }

                mv = ha.handle(req, res, mappedHandler.getHandler());

                mappedHandler.applyPostHandle(req, res, mv);

            } catch (Exception e) {
                dispatchException = e;
            }

            processDispatchResult(req, res, mappedHandler, mv, dispatchException);

        } catch (Exception e) {
            triggerAfterCompletion(req, res, mappedHandler, e);
        }
    }

    private HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception {
        for (HandlerMapping handlerMapping : handlerMappings) {
            HandlerExecutionChain handler = handlerMapping.getHandler(req);
            if (handler != null) return handler;
        }

        return null;
    }

    private void noHandlerFound(HttpServletRequest req, HttpServletResponse res) throws HandlerNotFoundException {
        res.setStatus(HttpStatus.NOT_FOUND.getCode());

        throw new HandlerNotFoundException("Not found Handler for " + req.getMethod() + " " + req.getRequestURI());
    }

    private HandlerAdapter getHandlerAdapter(Object handler) throws Exception {
        for (HandlerAdapter handlerAdapter : this.handlerAdapters) {
            boolean isSupport = handlerAdapter.supports(handler);
            if (isSupport) return handlerAdapter;
        }

        throw new RuntimeException();
    }

    private void render(ModelAndView mv, HttpServletRequest req, HttpServletResponse res) throws Exception {

        String viewName = mv.getViewName();

        log.debug("render view: {}", viewName);

        View view = resolveViewName(viewName, req);

        try {
            view.render(mv.getModelInternal(), req, res);
        } catch (Exception e) {
            log.debug("Failed rendering view [{}], {}", view, e);
            throw e;
        }

    }

    private View resolveViewName(String viewName, HttpServletRequest req) {
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveView(viewName);

            if (view != null) return view;
        }

        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse res, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception ex) throws Exception {
        if (ex != null) {
            if (ex instanceof HandlerNotFoundException) {
                mv = ((HandlerNotFoundException) ex).getModelAndView();
            }
            else mv = processHandlerException(req, res, mappedHandler, ex);
        }

        render(mv, req, res);

        triggerAfterCompletion(req, res, mappedHandler, ex);
    }

    private ModelAndView processHandlerException(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        ModelAndView exMv = null;

        if (this.handlerExceptionResolvers != null) {
            for (final HandlerExceptionResolver resolver : this.handlerExceptionResolvers) {
                exMv = resolver.resolveException(req, res, handler, ex);

                if (exMv != null) return exMv;
            }
        }

        throw ex;
    }

    private void triggerAfterCompletion(HttpServletRequest req, HttpServletResponse res, HandlerExecutionChain mappedHandler, Exception ex) {
        if (mappedHandler != null) mappedHandler.triggerAfterCompletion(req, res, ex);
    }

}
