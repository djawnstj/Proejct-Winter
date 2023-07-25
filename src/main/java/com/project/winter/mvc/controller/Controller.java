package com.project.winter.mvc.controller;

import com.project.winter.mvc.view.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controller {
    ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res);
}
