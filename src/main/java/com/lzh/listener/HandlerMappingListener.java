package com.lzh.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class HandlerMappingListener  implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //应用启动时加载数据

    }
}
