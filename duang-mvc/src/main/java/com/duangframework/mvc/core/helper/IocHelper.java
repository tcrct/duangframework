package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class IocHelper {

    private static Logger logger = LoggerFactory.getLogger(BeanHelper.class);

    private static Map<Class<?>, Object> beanMap = new HashMap<>();

    public static void duang() throws Exception {
        // 取出所有类实例
        beanMap = BeanUtils.getAllBeanMap();

        if(ToolsKit.isEmpty(beanMap)) { return; }

        /**
         * 遍历所有存在beanMap里的类对象
         * 先根据类方法是否有代理注解，如果有，则先将该类对类转换成代理类后再重新设置到beanMap里
         * 再根据类属性是否有注入注解，如果有，则将类对象注入到属性
         */
        for(Iterator<Map.Entry<Class<?>, Object>> it = beanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class<?>, Object> entry = it.next();
            ioc(entry.getKey());
        }
        logger.debug("IocHelper Success...");
    }


    public static void ioc(Class<?> beanClass) throws Exception {

        Field[] fields = beanClass.getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(Import.class)) {
                Class<?> fieldTypeClass = field.getType();
                if (fieldTypeClass.equals(beanClass)) {
                    throw new ServiceException(beanClass.getSimpleName() + " Can't Not Import From already!");
                }
                Object iocObj = BeanUtils.getBean(fieldTypeClass, beanClass);
                if(ToolsKit.isNotEmpty(iocObj)) {
                    field.setAccessible(true);
                    field.set(BeanUtils.getBean(beanClass, null), iocObj);
                }
            }
        }

    }

}
