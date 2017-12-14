package com.duangframework.rpc.core;

import com.duangframework.core.annotation.rpc.Rpc;
import com.duangframework.core.common.aop.ProxyManager;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.RpcException;
import com.duangframework.core.interfaces.IProxy;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.rpc.common.RpcAction;
import com.duangframework.rpc.server.RpcServer;
import com.duangframework.rpc.utils.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by laotang
 * @date on 2017/12/13.
 */
public class RpcFactory {

    private static final Logger logger = LoggerFactory.getLogger(RpcFactory.class);

    /**存放接口名与服务对象之间的映射关系 所有的服务实现都会用到*/
    private static final Map<String, RpcAction> HANDLER_MAP = new ConcurrentHashMap<>();
    public static Map<String, RpcAction> getRpcActionMap() {
        return HANDLER_MAP;
    }

    /**Rpc服务器*/
    private static RpcServer rpcServer;
    public static RpcServer getRpcServer() {
        return rpcServer;
    }

    /**
     * 初始化RPC服务， 发布到注册中心
     * @param classSet
     */
    public static void initService(Set<Class<?>> classSet) {
        if(ToolsKit.isEmpty(classSet)) {
            throw new EmptyNullException("rpc server class set is null");
        }
        for(Class<?> rpcInterfaceClass : classSet) {
            Rpc rpcAnnotation = rpcInterfaceClass.getAnnotation(Rpc.class);
            if(!rpcInterfaceClass.isInterface() && ToolsKit.isEmpty(rpcAnnotation)) {
                continue;
            }
            String interfaceName = rpcInterfaceClass.getName();
            String serviceFullPath = rpcAnnotation.service();
            if(ToolsKit.isEmpty(serviceFullPath)) {
                throw new EmptyNullException("serviceFullPath is empty");
            }
            HANDLER_MAP.put(interfaceName, new RpcAction(rpcInterfaceClass, ClassUtils.loadClass(serviceFullPath), rpcAnnotation));
        }
        if(ToolsKit.isEmpty(HANDLER_MAP)) {
            logger.warn("Rpc service is null, exit initService...");
            return;
        }
        // 启动Netty服务
        if(startRpcServer()) {
            // 发布服务
            publish();
        }
    }

    /**
     *  启动Netty
    *  @return
     */
    private static boolean startRpcServer() {
        FutureTask<Boolean> futureTask = ThreadPoolKit.execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                rpcServer = new RpcServer(RpcUtils.getHost(), RpcUtils.getPort());
                try {
                    rpcServer.start();
                    return true;
                } catch (Exception e) {
                    logger.warn("startRpcServer is fail: " + e.getMessage(), e);
                    rpcServer.shutdown();
                    return false;
                }
            }
        });
        try {
            return futureTask.get(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 发布服务
     */
    private static void publish() {
        // 节点数据的全路径
        String path = RpcUtils.getZookNoteFullPath();

    }

    /**
     * 初始化RPC客户端，即消费者，生成代理类
     * @param classSet
     */
    public static void initClient(Set<Class<?>> classSet) {
        for(Class<?> clientCls : classSet) {
            if (!clientCls.isInterface()){
                continue;
            }
            Object proxyObj = newProxyInstance(clientCls);
            if (null != proxyObj) {
                BeanUtils.setBean2Map(clientCls, proxyObj); // 再将该代理类存放在BeanUtils里，让框架在执行IocHepler时注入到对应的Service
            }
        }
    }

    /**
     * 通过反射创建代理实例
     */
    public static <T> T newProxyInstance(Class<?> commandClass) {
        try {
            List<IProxy> proxyList = new ArrayList<IProxy>();
            proxyList.add(new RpcClientProxy());
            return ProxyManager.createProxy(commandClass, proxyList);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

}
