package com.duangframework.rpc.plugin;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.exceptions.RpcException;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.rpc.core.RpcFactory;
import com.duangframework.rpc.utils.AutoBuildServiceInterface;
import com.duangframework.rpc.utils.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Created by laotang
 * @date on 2017/12/6.
 */
public class RpcPlugin implements IPlugin {

    private static final Logger logger = LoggerFactory.getLogger(RpcPlugin.class);
    @Override
    public void init() throws Exception {
        // 先创建需要发布的的接口类
        String rpcModulePath = ConfigKit.duang().key("rpc.module.path").asString();
        if(ToolsKit.isEmpty(rpcModulePath)) {
            throw new RpcException("rpc module path is empty");
        }
        // 服务提供者目录
        rpcModulePath += rpcModulePath.endsWith("/") ? "provider" : "/provider";
        RpcUtils.autoCreateBatchInterface(rpcModulePath);
        // 先发布所有提供者服务到ZK
        RpcUtils.pushProviderServiceSource();
    }

    @Override
    public void start() throws Exception {
        Map<Class<?>, Object> controllerMap = BeanUtils.getAllBeanMaps().get(Controller.class.getSimpleName());
        Map<Class<?>, Object> serviceMap = BeanUtils.getAllBeanMaps().get(Service.class.getSimpleName());
        Map<Class<?>, Object> rpcServiceMap = AutoBuildServiceInterface.getInterfaceClassMap(); //BeanUtils.getAllBeanMaps().get(Rpc.class.getSimpleName());
        if(ToolsKit.isEmpty(rpcServiceMap)) {
            logger.warn("RpcPlugin start:  rpcServiceMap is null, so return..."  );
            return;
        }
        Set<Class<?>> rpcClassSet = new HashSet<>();
        if(ToolsKit.isNotEmpty(controllerMap)) {rpcClassSet.addAll(controllerMap.keySet());}
        if(ToolsKit.isNotEmpty(serviceMap)) {rpcClassSet.addAll(serviceMap.keySet());}
        Set<String> excludeMethodNameSet = ObjectKit.buildExcludedMethodName();
        // 生产者，需要发布到注册中心
        Set<Class<?>> rpcServiceSet = rpcServiceMap.keySet();
        Set<Class<?>> rpcClientSet = new HashSet<>();
        for(Iterator<Class<?>> iterator = rpcClassSet.iterator(); iterator.hasNext();) {
            Class<?> cls = iterator.next();
            // 遍历类属性，确定消费者
            Field[] fields = cls.getDeclaredFields();
            for(Field field : fields) {
                if(excludeMethodNameSet.contains(field.getName())) {
                    continue;
                }
                // 如果是import注解且是RpcService集合是有的，则认为是消费者
                if(field.isAnnotationPresent(Import.class) && rpcServiceSet.contains(field.getType())) {
                    rpcClientSet.add(field.getType());
                }
            }
        }

        // 初始化服务生产者
        RpcFactory.initService(rpcServiceSet);
        if(ToolsKit.isNotEmpty(rpcClientSet)) {
            // 初始化服务消费者
            RpcFactory.initClient(rpcClientSet);
            // 监听节点
            RpcFactory.watchNode();
        }

    }

    @Override
    public void stop() throws Exception {
        RpcFactory.getRpcServer().shutdown();
    }
}
