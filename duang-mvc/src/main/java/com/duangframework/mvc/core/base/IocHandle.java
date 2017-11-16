package com.duangframework.mvc.core.base;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Monitor;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class IocHandle {

    private static Logger logger = LoggerFactory.getLogger(IocHandle.class);

    public static void duang() throws Exception {
        Map<String, Object> beanMap = InstanceFactory.getAllBeanMap();
        if(ToolsKit.isEmpty(beanMap)) { return; }
        for(Iterator<Map.Entry<String, Object>> it = beanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            Object beanObj = entry.getValue();
            // IOC注入
            Field[] fields = beanObj.getClass().getDeclaredFields();
            for(Field field : fields) {
                for(Iterator<Class<? extends Annotation>> annotIt =  InstanceFactory.IOC_ANNOTATION_SET.iterator(); annotIt.hasNext();) {
                    Class<? extends Annotation> annotationClass = annotIt.next();
                    if (field.isAnnotationPresent(annotationClass)) {
                        Object iocObj = beanMap.get(field.getType().getCanonicalName());
                        if(ToolsKit.isNotEmpty(iocObj)) {
                            field.setAccessible(true);
                            field.set(beanObj, iocObj);
                        }
                    }
                }
            }
            // 设置AOP
            Method[] methods = beanObj.getClass().getDeclaredMethods();
            for(Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                if(ToolsKit.isNotEmpty(annotations)) {
                    List<Object> proxyList = new ArrayList<>(annotations.length);
                    for(Annotation aopAnnot : annotations) {

                    }
                }
            }
        }






        List<Class<?>> classList = new ArrayList<>();
        classList.addAll(InstanceFactory.getAllBeanMap().get(Controller.class.getSimpleName()));
        classList.addAll(InstanceFactory.getAllBeanMap().get(Service.class.getSimpleName()));
        classList.addAll(InstanceFactory.getAllBeanMap().get(Monitor.class.getSimpleName()));
        // 实例化

        for(Iterator<Class<?>> it = classList.iterator(); it.hasNext();) {
            Class<?> clazz = it.next();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                field.getAnnotation(Import.class);
            }
        }

    }

}
