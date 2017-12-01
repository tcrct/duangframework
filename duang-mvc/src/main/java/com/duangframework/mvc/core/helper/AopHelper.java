package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
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
        Map<Class<?>, Object> proxyMap = BeanUtils.getAllBeanMaps().get(key);
        if(ToolsKit.isEmpty(proxyMap)) { return; }
        Map<Class<?>, Object> proxyMapNew = new HashMap<>(proxyMap.size());
        for(Iterator<Map.Entry<Class<?>, Object>> it = proxyMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class<?>, Object> entry = it.next();
            Class<?> proxyClass = entry.getKey();
            Proxy proxy = proxyClass.getAnnotation(Proxy.class);
            Class<?> aopClass = proxy.aop();
            // 以Proxy注解里的aop字段值作为key, 以供在IocHelper里，执行AOP代理时，根据方法上的注解名称取回对应的代理对象
            proxyMapNew.put(aopClass, entry.getValue());
        }
        if (ToolsKit.isNotEmpty(proxyMapNew)) {
            // 将原来的删除掉
            BeanUtils.getAllBeanMaps().remove(key);
            // 重新缓存
            BeanUtils.setAllBeanMaps(key, proxyMapNew);
        }

        // DB??


        logger.warn("AopHelper Success...");

    }

}
