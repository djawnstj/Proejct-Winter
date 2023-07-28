package com.project.winter;

import com.project.winter.beans.BeanFactoryUtils;
import com.project.winter.exception.handler.HandlerNotFoundException;
import com.project.winter.mvc.handler.HandlerExecutionChain;
import com.project.winter.mvc.handler.adapter.HandlerAdapter;
import com.project.winter.mvc.handler.adapter.RequestMappingHandlerAdapter;
import com.project.winter.mvc.handler.adapter.SimpleControllerHandlerAdapter;
import com.project.winter.mvc.handler.mapping.HandlerMapping;
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

    @Override
    public void init() throws ServletException {
        log.info("DispatcherServlet init() called.");

        initHandlerMappings();
        initHandlerAdapters();
        initViewResolvers();
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

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("DispatcherServlet service() called.");

        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse res) {

        HandlerExecutionChain mappedHandler = null;
        Exception dispatchException = null;

        try {
            mappedHandler = getHandler(req);

            if (mappedHandler == null) throw new HandlerNotFoundException();

            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            if (!mappedHandler.applyPreHandle(req, res)) {
                return ;
            }

            ModelAndView mv = ha.handle(req, res, mappedHandler.getHandler());

            render(mv, req, res);

            mappedHandler.applyPostHandle(req, res, mv);

        } catch (Exception e) {
            if (e instanceof HandlerNotFoundException) res.setStatus(HttpStatus.NOT_FOUND.getCode());
            dispatchException = e;
            // TODO ExceptionResolver 개발시 예외 발생시 추가 로직 필요
        } finally {
            if (mappedHandler != null) mappedHandler.triggerAfterCompletion(req, res, dispatchException);
        }
    }

    private HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception {
        for (HandlerMapping handlerMapping : handlerMappings) {
            HandlerExecutionChain handler = handlerMapping.getHandler(req);
            if (handler != null) return handler;
        }

        return null;
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
}
