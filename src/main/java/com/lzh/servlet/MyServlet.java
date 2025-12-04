package com.lzh.servlet;

import com.lzh.listener.HandlerMappingListener;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class MyServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //不包含协议、主机、接口
        String uri = req.getRequestURI();
        //判断uri是否存在对应的方法是否存在
        Map<String, Method> controllerMap = HandlerMappingListener.CONTROLLER_MAP;
        if (!controllerMap.containsKey(uri)) {
            System.out.println(404);
            return;
        }
        Method method = controllerMap.get(uri);
        //判断参数的数量
        int parameterCount = method.getParameterCount();
        if (parameterCount==0){
            //无参
        }else{
            //多个参数
        }
    }
}
