package com.duangframework.mvc.listener;

import com.duangframework.core.exceptions.ServletException;
import com.duangframework.mvc.core.helper.*;

/**
 *
 * @author laotang
 * @date 2017/11/5
 */
public class ContextLoaderListener {

    private String context;

    public ContextLoaderListener() {
        try {
            contextInitialized();
        } catch (Exception e) {
            throw new ServletException("ContextLoaderListener init is fail :  " + e.getMessage() + " ,exit...", e ) ;
        }
    }

    /**
     * 容器销毁时监听
     */
    public void contextDestroyed() {
    }

    /**
     * 初始化MVC框架，各方法的执行顺序不能变，不能变，不能变
     * @throws Exception
     */
    public void contextInitialized() throws Exception {
        initContext();
        // 扫描.class文件，并且将class实例化成bean对象，以供使用
        BeanHelper.duang();
        // 初始化插件
        PluginHelper.duang();
        // 初始化Aop, 基于代理的实现
        AopHelper.duang();
         // 初始化Ioc, 依赖注入, 主要在Controller里注入Service类， Service里注入Service类
        IocHelper.duang();
        // 初始化路由
        RouteHelper.duang();
    }

    /**
     *  初始化 IDuang
     * @throws Exception
     */
    private void initContext()  throws Exception {
        System.out.println("###################:  initContext");
    }
}
