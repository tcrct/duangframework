package com.duangframework.mvc.listener;

import com.duangframework.core.exceptions.ServletException;
import com.duangframework.mvc.core.base.AopHandle;
import com.duangframework.mvc.core.base.BeanHandle;
import com.duangframework.mvc.core.base.IocHandle;

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
     * 初始化MVC框架，各方法的执行顺序不能变
     * @throws Exception
     */
    public void contextInitialized() throws Exception {
        initContext();
        initBean();
        initPlugins();
        initAop();
        initIoc();
    }

    /**
     *  初始化 IDuang
     * @throws Exception
     */
    private void initContext()  throws Exception {
        System.out.println("###################:  initContext");
    }

    /**
     *  扫描.class文件，并且将class实例化成bean对象，以供使用
     */
    private  void initBean()  throws Exception {
        BeanHandle.duang();
    }

    /**
     *  初始化插件
     */
    private void initPlugins()  throws Exception {

    }

    /**
     *  初始化Aop
     *  基于代理的实现
     */
    private void initAop()  throws Exception {
        AopHandle.duang();
    }

    /**
     * 初始化Ioc, 依赖注入
     * 主要在Controller里注入Service类， Service里注入Service类
     */
    private void initIoc()  throws Exception {
        IocHandle.duang();
    }

}
