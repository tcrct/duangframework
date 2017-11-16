package com.duangframework.mvc.core.base;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class AopHandle {

    private static Logger logger = LoggerFactory.getLogger(AopHandle.class);

    public static void duang() {
        List<Object> proxyList = InstanceFactory.getAllBeanMap().get(Proxy.class.getSimpleName());
        if(ToolsKit.isEmpty(proxyList)) {
            return;
        }
        Map<String, Class<?>> proxyMap = new HashMap<>();
        for(Iterator<Class<?>> it = proxyList.iterator(); it.hasNext();) {
            Class<?> proxyClass = it.next();
            Proxy proxy = proxyClass.getAnnotation(Proxy.class);
            String key = proxy.aop().getCanonicalName();
            Class<?> proxyBean = ClassUtils.loadClass(proxyClass, true);
            proxyMap.put(key, proxyBean.newInstance());
        }
        // 缓存到对象实例工厂
        InstanceFactory.setProxyMap(proxyMap);
    }

}
