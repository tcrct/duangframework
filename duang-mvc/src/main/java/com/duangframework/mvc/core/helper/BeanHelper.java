package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.common.aop.ProxyManager;
import com.duangframework.core.exceptions.MvcStartUpException;
import com.duangframework.core.interfaces.IProxy;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.kit.ClassScanKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class BeanHelper {

    private static Logger logger = LoggerFactory.getLogger(BeanHelper.class);

    private static final Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();

    public static void duang() {
        //扫描指定包路径下的类文件，类文件包含有指定的注解类或文件名以指定的字符串结尾的
        Map<String, List<Class<?>>> classMap =  ClassScanKit.duang()
                .annotations(InstanceFactory.MVC_ANNOTATION_SET)
                .packages(ConfigKit.duang().key("base.package.path").asArray())
                .jarname(ConfigKit.duang().key("jar.prefix").asArray())
                // 增加MVC固定扫描的包路径
                .packages("com.duangframework.mvc")
                .map();

        if(ToolsKit.isNotEmpty(classMap)) {
            String proxyKey = Proxy.class.getSimpleName();
            // 找出所有代理类进行实例化并缓存
            Map<Class<? extends Annotation>, IProxy> annotationMap = new HashMap<>();
            List<Class<?>> proxyList = classMap.get(proxyKey);
            if(ToolsKit.isNotEmpty(proxyList)) {
                for (Class<?> proxyClass : proxyList) {
                    Proxy proxy = proxyClass.getAnnotation(Proxy.class);
                    if (ToolsKit.isNotEmpty(proxy)) {
                        Class<? extends Annotation> aopClass = proxy.aop();
                        IProxy proxyObj = ClassUtils.newInstance(proxyClass);
                        annotationMap.put(aopClass, proxyObj);
                    }
                }
            }
            // 将扫描后的Class进行实例化并缓存(Proxy除外)
            try {
                for(Iterator<Map.Entry<String, List<Class<?>>>> it = classMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, List<Class<?>>> entry = it.next();
                    String key = entry.getKey();
                    if(proxyKey.equals(key)) {
                        continue;
                    }
                    List<Class<?>> classList = entry.getValue();
                    if(ToolsKit.isEmpty(classList)) { continue; }
                    Map<Class<?>, Object> subBeanMap = BeanUtils.getAllBeanMaps().get(key);
                    if(ToolsKit.isEmpty(subBeanMap)) { subBeanMap = new HashMap<>(classList.size());}
                    for(Class<?> cls : classList) {
                        Object clsObj = createBean(annotationMap, cls);
                        if(clsObj != null) {
                            // 实例化后，用类全名作key， 实例化对象作value缓存起来
                            subBeanMap.put(cls, clsObj);
                        }
                    }
                    BeanUtils.setAllBeanMaps(key, subBeanMap);
                }
            } catch (Exception e) {
                throw new MvcStartUpException(e.getMessage(), e);
            }
        }
        logger.warn("BeanHelper Success...");
    }

    private static Object createBean(Map<Class<? extends Annotation>, IProxy> annotationMap, Class<?> cls) throws Exception {
        List<IProxy> proxyList = new ArrayList<>();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if(excludedMethodName.contains(method.getName())) {
                continue;
            }
            if(ToolsKit.isNotEmpty(annotationMap)) {
                for (Iterator<Map.Entry<Class<? extends Annotation>, IProxy>> it = annotationMap.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Class<? extends Annotation>, IProxy> entry = it.next();
                    if (method.isAnnotationPresent(entry.getKey())) {
                        proxyList.add(entry.getValue());
                    }
                }
            }
        }
        Object instanceObj = null;
        // 实例化类，如果有代理注解则创建代理类
        if (ToolsKit.isNotEmpty(proxyList)) {
            // 代理类
            instanceObj = ProxyManager.createProxy(cls, proxyList);
        } else {
            instanceObj = ClassUtils.newInstance(cls);
        }
        return instanceObj;
    }


}
