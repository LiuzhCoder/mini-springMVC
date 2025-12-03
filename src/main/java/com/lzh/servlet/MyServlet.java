package com.lzh.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //不包含协议、主机、接口
        String uri = req.getRequestURI();
        //找到页面控制器，这一步需要先将所有的控制器都存储在一个Map中
        //找到方法的参数
        //封装参数
        //调用方法
        //方法会返回一个 String 的字符串，这个字符串对应了视图
    }
}
