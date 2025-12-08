package com.lzh.listener;

import com.lzh.annotation.MyController;
import com.lzh.annotation.MyRequestMapping;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class HandlerMappingListener  implements ServletContextListener {
    /**
     * 存储找到的所有类名
     */
    private final List<String> classNames = new ArrayList<>();

    public final static Map<String, Method> CONTROLLER_MAP = new HashMap<>();
    public final static Map<String, Object> CONTROLLER_BEANS = new HashMap<>();
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //应用启动时加载数据
        // 1. 获取当前项目的 Classpath 根目录 (即 WEB-INF/classes)
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        if (url == null) {
            System.err.println("无法获取 classpath，扫描失败");
            return;
        }
        // 处理 URL 中的空格等特殊字符 (如 %20)
        File rootDir = null;
        try {
            rootDir = new File(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // 2. 开始从根目录递归扫描
        // 这里的 packageName 传空字符串，表示从根包开始
        doScan(rootDir, "");

        System.out.println("扫描结束，共发现类：" + classNames.size() + " 个");
    }

    private void doScan(File dir, String packageName) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，拼接包名，继续递归
                // 比如当前是 com，下一级是 lzh -> com.lzh
                String newPackageName = packageName.equals("") ? file.getName() : packageName + "." + file.getName();
                doScan(file, newPackageName);
            } else {
                // 如果是文件，判断是不是 .class
                if (file.getName().endsWith(".class")) {
                    // 去掉后缀 .class
                    String className = file.getName().replace(".class", "");
                    // 拼接完整类名
                    String fullClassName = packageName.equals("") ? className : packageName + "." + className;

                    // 添加到列表（或者在这里直接反射判断有没有 @Controller 注解）
                    classNames.add(fullClassName);
                    checkController(fullClassName);
                }
            }
        }
    }

    private void checkController(String fullClassName) {
        try {
            Class<?> clazz = Class.forName(fullClassName);
            // 假设你自己定义了一个 MyController 注解
            if (clazz.isAnnotationPresent(MyController.class)) {
                Object controller = clazz.getDeclaredConstructor().newInstance();
                Method[] declaredMethods = clazz.getDeclaredMethods();
                String basePath = "";
                if (clazz.isAnnotationPresent(MyRequestMapping.class)){
                    basePath = clazz.getAnnotation(MyRequestMapping.class).value();
                }
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.isAnnotationPresent(MyRequestMapping.class)){
                        MyRequestMapping annotation = declaredMethod.getAnnotation(MyRequestMapping.class);
                        CONTROLLER_MAP.put(basePath+annotation.value(),declaredMethod);
                        CONTROLLER_BEANS.put(basePath+annotation.value(),controller);
                    }
                }

            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
