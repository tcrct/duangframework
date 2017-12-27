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
import com.duangframework.core.utils.IpUtils;
import com.duangframework.rpc.common.RpcAction;
import com.duangframework.rpc.server.RpcServer;
import com.duangframework.rpc.utils.RpcUtils;
import com.duangframework.zookeeper.kit.ZooKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    // RpcAction集合
    private static final Map<String, List<RpcAction>> actionMap = new ConcurrentHashMap<>();
    // 监听节点路径集合，根据消费者端所需要的节点进行监听
    private static final Set<String> watchNodePath = new HashSet<>();
    // 监听对象
    private static ZooKeeperListener zooKeeperListener;

    /**
     * 初始化RPC服务， 发布到注册中心
     * @param classSet
     */
    public static void initService(Set<Class<?>> classSet) throws Exception {
        if(ToolsKit.isEmpty(classSet)) {
            throw new EmptyNullException("rpc server class set is null");
        }
        for(Class<?> rpcInterfaceClass : classSet) {
            Rpc rpcAnnotation = rpcInterfaceClass.getAnnotation(Rpc.class);
            if(!rpcInterfaceClass.isInterface() || ToolsKit.isEmpty(rpcAnnotation)) {
                continue;
            }
            String interfaceName = rpcInterfaceClass.getName();
            String serviceName = rpcAnnotation.service();
            if(ToolsKit.isEmpty(serviceName)) {
                throw new EmptyNullException("serviceFullPath is empty");
            }
            HANDLER_MAP.put(interfaceName, new RpcAction(ClassUtils.loadClass(serviceName),
                    rpcInterfaceClass,
                    IpUtils.getLocalHostIP(false),
                    IpUtils.getLocalHostIP(true),
                    RpcUtils.getPort()
                    ));
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
        try {
            ThreadPoolKit.execute(new Runnable() {
                @Override
                public void run() {
                    rpcServer = new RpcServer(RpcUtils.getHost(), RpcUtils.getPort());
                    try {
                        rpcServer.start();
                    } catch (Exception e) {
                        throw new RpcException("startRpcServer is fail: " + e.getMessage(), e);
                    } finally {
                        rpcServer.shutdown();
                    }
                }
            });
            return true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
           throw new RpcException("start rpc server is fail : " + e.getMessage(), e);
        }
    }

    /**
     * 发布服务
     */
    private static void publish() throws Exception{
        // 节点数据的全路径
        String path = RpcUtils.getZookNoteFullPath();
        //注册到ZK
        ZooKit.duang().path(path).data(HANDLER_MAP).set();
    }

    /**
     * 初始化RPC客户端，即消费者，生成代理类
     * @param classSet
     */
    public static void initClient(Set<Class<?>> classSet) {
        if(ToolsKit.isEmpty(classSet)) { return; }
        try {
            for(Class<?> clientCls : classSet) {
                if (!clientCls.isInterface()) {
                    continue;
                }
                List<IProxy> proxyList = new ArrayList<>();
                proxyList.add(new RpcClientProxy());
                Object proxyObj = ProxyManager.createProxy(clientCls, proxyList);
                if (null != proxyObj) {
                    // 再将该代理类存放在BeanUtils里，让框架在执行IocHepler时注入到对应的Service
                    BeanUtils.setBean2Map(clientCls, proxyObj);
                    // 取出产品代号
                    Rpc rpc = clientCls.getAnnotation(Rpc.class);
                    watchNodePath.add(RpcUtils.getZookNotePath(rpc.productcode()));
                }
            }
        } catch (Exception e) {
            throw new RpcException("RpcFactory newProxyInstance is fail : "+ e.getMessage(), e);
        }
    }

    public static void watchNode() {
        for(Iterator<String> iterator = watchNodePath.iterator(); iterator.hasNext();) {
            String path = iterator.next();
            if(ToolsKit.isEmpty(path)) { continue; }
            boolean isExists = ZooKit.duang().path(path).exists();
            if(!isExists){
                throw new RpcException("zookeeper note["+path+"]  is not exists");
            }
            List<String> nodeList = ZooKit.duang().path(path).children();
            if (ToolsKit.isEmpty(nodeList)) {
                throw new RpcException( "zookeeper children note["+path+"]  is empty");
            }
            for(String nodePath : nodeList) {
                String subPath = path + "/" + nodePath;
                String jsonText = ZooKit.duang().path(subPath).get();
                Map<String, Object> map  = ToolsKit.jsonParseObject(jsonText,  Map.class);
                if(ToolsKit.isNotEmpty(map)) {
                    for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<String,Object> entry = it.next();
                        String key = entry.getKey();
                        String value = entry.getValue()+"";
                        RpcAction rpcAction = ToolsKit.jsonParseObject(value,  RpcAction.class);
                        addRpcAction2List(key, rpcAction);
                    }
                }
            }
        }
        // 如果有指定的调用服务器时 2017-7-13
        Map<String, List<RpcAction>> actionMapNew = RpcUtils.getAssignRpcActionMap(actionMap);
        if(ToolsKit.isNotEmpty(actionMapNew)) {
            actionMap.clear();
            actionMap.putAll(actionMapNew);
            logger.warn("AssignRpcActionEndport:  " + ToolsKit.toJsonString(actionMap));
        }
        // 监听该节点下的所有目录
        if(ToolsKit.isEmpty(zooKeeperListener)) {
            zooKeeperListener = new ZooKeeperListener(watchNodePath);
            zooKeeperListener.startListener();
        }
    }

    private static void addRpcAction2List(String key , RpcAction action) {
        if(actionMap.containsKey(key)) {
            actionMap.get(key).add(action);
        } else {
            List<RpcAction> tmpList = new ArrayList<>();
            tmpList.add(action);
            actionMap.put(key, tmpList);
        }
    }

    /**
     * 根据clazz对象查找出对应的RpcAction对象
     * @param cls
     * @return
     */
    public static RpcAction discoverService(Class<?> cls) {
        String key = cls.getName();
        List<RpcAction> actionList = actionMap.get(key);
        if(ToolsKit.isNotEmpty(actionList)) {
            int size = actionList.size();
            return (size == 1) ? actionList.get(0) : actionList.get(RpcUtils.getRandomBySize(size)); //随机下标
        }
        return null;
    }

}
