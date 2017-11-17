package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  AOP帮助类
 *  将在allBeanMap里key为Proxy的集合取出，再根据代理类对象里的Proxy注解，取出aop字段值，再根据该字段值重新设置Map的KV关系
 *
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class AopHelper {

    private static Logger logger = LoggerFactory.getLogger(AopHelper.class);

    public static void duang() {
        String key = Proxy.class.getSimpleName();
        Map<String, Object> proxyMap = InstanceFactory.getAllBeanMaps().get(key);
        if(ToolsKit.isEmpty(proxyMap)) { return; }
        Map<String, Object> proxyMapNew = new HashMap<>(proxyMap.size());
        for(Iterator<Map.Entry<String,Object>> it = proxyMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String,Object> entry = it.next();
            Object proxyObj = entry.getValue();
            Proxy proxy = proxyObj.getClass().getAnnotation(Proxy.class);
            String aopKey = proxy.aop().getCanonicalName();
            // 以Proxy注解里的aop字段值作为key, 以供在IocHelper里，执行AOP代理时，根据方法上的注解名称取回对应的代理对象
            proxyMapNew.put(aopKey, proxyObj);
        }
        if (ToolsKit.isNotEmpty(proxyMapNew)) {
            // 将原来的删除掉
            InstanceFactory.getAllBeanMaps().remove(key);
            // 缓存到对象实例工厂
            InstanceFactory.setAllBeanMaps(key, proxyMapNew);
        }

        // DB??


        logger.warn("AopHelper Success...");

    }

}
