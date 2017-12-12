package com.duangframework.mvc.listener;

import com.duangframework.core.exceptions.MvcStartUpException;
import com.duangframework.core.interfaces.IContextLoaderListener;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.IDuang;
import com.duangframework.mvc.core.helper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author laotang
 * @date 2017/11/5
 */
public class ContextLoaderListener implements IContextLoaderListener{

    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

    private static IDuang duangFrameword = null;

    @Override
    public void start() {
        try {
            contextInitialized();
        } catch (Exception e) {
            throw new MvcStartUpException("ContextLoaderListener init is fail :  " + e.getMessage() + " ,exit...", e ) ;
        }
    }

    /**
     * 容器销毁时监听
     */
    public void contextDestroyed() {
        PluginHelper.stop();
    }

    /**
     * 初始化MVC框架，各方法的执行顺序不能变，不能变，不能变
     * @throws Exception
     */
    public void contextInitialized() throws Exception {
        initContext();
        // 扫描.class文件
        BeanHelper.duang();
        // 初始化插件
        PluginHelper.duang();
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
        String configClass = ConfigKit.duang().key("mvc.config").asString();
        if (ToolsKit.isEmpty(configClass)) {
            throw new MvcStartUpException("IDuang子类路径不能为空!");
        }
        try {
            duangFrameword = ObjectKit.newInstance(configClass);
            duangFrameword.addHandlers();
            duangFrameword.addPlugins();
            logger.warn("initContext success");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new MvcStartUpException("不能创建类: " + configClass, e);
        }
    }
}
