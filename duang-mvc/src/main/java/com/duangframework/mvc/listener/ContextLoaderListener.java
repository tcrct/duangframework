package com.duangframework.mvc.listener;

import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.common.Const;
import com.duangframework.core.exceptions.ServletException;
import com.duangframework.mvc.kit.ClassScanKit;

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

    // 初始化MVC框架，各方法的执行顺序不能变
    public void contextInitialized() throws Exception {
        initContext();
        initBean();
        initPlugins();
        initAop();
        initIoc();
    }

    // 初始化 IDuang
    private void initContext()  throws Exception {

    }

    // 扫描.class文件，并且将class实例化成bean对象，以供使用
    private  void initBean()  throws Exception {
        //扫描指定包路径下的类文件，类文件包含有指定的注解类或文件名以指定的字符串结尾的
        ClassScanKit.duang().annotation(Controller.class).annotation(Service.class)
                .packages("")
                .jarname("")
                .suffix(Const.CONTROLLER_ENDWITH_NAME).suffix(Const.SERVICE_ENDWITH_NAME)
                .list();
    }

    // 初始化插件
    private void initPlugins()  throws Exception {

    }

    // 初始化Aop
    private void initAop()  throws Exception {

    }

    // 初始化Ioc
    private void initIoc()  throws Exception {

    }

}
