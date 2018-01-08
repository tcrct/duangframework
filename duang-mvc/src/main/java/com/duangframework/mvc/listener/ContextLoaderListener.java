package com.duangframework.mvc.listener;

import com.duangframework.core.exceptions.MvcStartUpException;
import com.duangframework.core.interfaces.IContextLoaderListener;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.IDuang;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.core.helper.BeanHelper;
import com.duangframework.mvc.core.helper.IocHelper;
import com.duangframework.mvc.core.helper.PluginHelper;
import com.duangframework.mvc.core.helper.RouteHelper;
import com.duangframework.mvc.handles.Handles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author laotang
 * @date 2017/11/5
 */
public class ContextLoaderListener implements IContextLoaderListener{

    private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);
    private static Lock _lock = new ReentrantLock();
    private static ContextLoaderListener _contextLoaderListener;

    private static IDuang duangFrameword = null;

    public static ContextLoaderListener getInstantiation() {
        if(null == _contextLoaderListener) {
            try {
                _lock.lock();
                _contextLoaderListener = new ContextLoaderListener();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _lock.unlock();
            }
        }
        return _contextLoaderListener;
    }

    private ContextLoaderListener() {
    }

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
    public static void contextDestroyed() {
        PluginHelper.stop();
        InstanceFactory.getHandles().clear();
//        ThreadPoolKit.shutdown();
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
        // 初始化其它工具类
        newInstanceDuang();
    }

    /**
     *  初始化 IDuang
     * @throws Exception
     */
    private void initContext()  throws Exception {
        String configClass = PropertiesKit.duang().key("mvc.config").asString();
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

    /**
     * 框架启动到最后执行的方法
     */
    private void newInstanceDuang() {
        duangFrameword.initDuang();
        // 用于设置框架必要的Handle处理器， 如将ActionHandle添加到最后等
        Handles.init();
        logger.warn("instance " + duangFrameword.getClass().getName() + " success!");

        long halfTimeOut = 10000L;
        new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        contextDestroyed();
                        contextInitialized();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }, halfTimeOut, halfTimeOut);

    }
}
