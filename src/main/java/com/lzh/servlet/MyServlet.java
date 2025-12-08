package com.lzh.servlet;

import com.lzh.annotation.MyRequestParam;
import com.lzh.listener.HandlerMappingListener;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class MyServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //不包含协议、主机、接口
        String uri = req.getRequestURI();
        //判断uri是否存在对应的方法是否存在
        Map<String, Method> controllerMap = HandlerMappingListener.CONTROLLER_MAP;
        Map<String,Object> controllerBeans = HandlerMappingListener.CONTROLLER_BEANS;
        if (!controllerMap.containsKey(uri)) {
            System.out.println(404);
            return;
        }
        Method method = controllerMap.get(uri);
        //判断参数的数量
        int parameterCount = method.getParameterCount();
        //参数列表
        Object[] args = new Object[parameterCount];
        if (parameterCount==0){
            //无参
        }else{
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                // 1. 特殊参数：req / resp
                if (type == HttpServletRequest.class) {
                    args[i] = req;
                    continue;
                }
                if (type == HttpServletResponse.class) {
                    args[i] = resp;
                    continue;
                }
                String parameterName;
                if (parameter.isAnnotationPresent(MyRequestParam.class)){
                    MyRequestParam annotation = parameter.getAnnotation(MyRequestParam.class);
                    parameterName = annotation.value();
                }else{
                    parameterName = parameter.getName();
                }
                //通过 request 获取参数
                String value = req.getParameter(parameterName);
                // 这里只做最简单的：String 直接用
                // 如果你以后要支持 int、long 等，可以在这里加类型转换
                args[i] = value;
            }
        }

        Object o = controllerBeans.get(uri);
        try {
            Object invoke = method.invoke(o, args);
            //判断是不是 String
            if (invoke instanceof String){
                //如果是，则去找对应的视图
                RequestDispatcher requestDispatcher = req.getRequestDispatcher((String) invoke);
                requestDispatcher.forward(req,resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
