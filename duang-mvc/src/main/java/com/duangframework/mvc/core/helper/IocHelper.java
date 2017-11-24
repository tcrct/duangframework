package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.common.aop.ProxyManager;
import com.duangframework.core.interfaces.IProxy;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * IOC注入类
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class IocHelper {

    private static Logger logger = LoggerFactory.getLogger(IocHelper.class);

    public static void duang() throws Exception {
        // 取出所有类对象
        Map<String, Object> beanMap = InstanceFactory.getAllBeanMap();
        // 取出所有代理类对象
        Map<String, Object> proxyMap = InstanceFactory.getAllBeanMaps().get(Proxy.class.getSimpleName());
        if(ToolsKit.isEmpty(beanMap)) { return; }
        /**
         * 遍历所有存在beanMap里的类对象
         * 先根据类方法是否有代理注解，如果有，则先将该类对类转换成代理类后再重新设置到beanMap里
         * 再根据类属性是否有注入注解，如果有，则将类对象注入到属性
          */
        for(Iterator<Map.Entry<String, Object>> it = beanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object beanObj = entry.getValue();
            Class<?> beanClass = beanObj.getClass();
            // 先设置AOP，因为有可能会产生代理类
            Method[] methods = beanClass.getDeclaredMethods();
            for(Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                if(ToolsKit.isNotEmpty(proxyMap) && ToolsKit.isNotEmpty(annotations)) {
                    List<IProxy> proxyList = new ArrayList<>(annotations.length);
                    for(Annotation annotation : annotations) {
                        IProxy proxyObj = (IProxy)proxyMap.get(annotation.annotationType().getCanonicalName());
                        if(ToolsKit.isNotEmpty(proxyObj)) {
                            proxyList.add(proxyObj);
                        }
                    }
                    // 代理类链管理
                    Object proxyManagerObj = ProxyManager.createProxy(beanClass, proxyList);
                    // 替换原来对象成代理类
                    beanMap.put(key, proxyManagerObj);
                }
            }
            // 再IOC注入
           ioc(beanMap, beanObj);
        }
        logger.debug("IocHelper Success...");
    }

    private static void ioc(Map<String, Object> beanMap, Object beanObj) throws Exception {
        Class<?> beanClass = beanObj.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for(Field field : fields) {
            // 注入Server
            if (field.isAnnotationPresent(Import.class)) {
                Object iocObj = beanMap.get(field.getType().getCanonicalName());
                if(ToolsKit.isNotEmpty(iocObj)) {
                    field.setAccessible(true);
                    field.set(beanObj, iocObj);
                }
            }

        }
    }

    /**
     * 依赖注入
     * @param beanObj
     * @throws Exception
     */
    public static void ioc(Object beanObj) throws Exception {
        ioc(InstanceFactory.getAllBeanMap(), beanObj);
    }

}
