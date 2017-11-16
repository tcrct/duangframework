package com.duangframework.mvc.core.base;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.annotation.mvc.Monitor;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class IocHandle {

    private static Logger logger = LoggerFactory.getLogger(IocHandle.class);

    public static void duang() {
        List<Class<?>> classList = new ArrayList<>();
//        classList.addAll(InstanceFactory.getAllClassMap().get(Controller.class.getSimpleName()));
        classList.addAll(InstanceFactory.getAllClassMap().get(Service.class.getSimpleName()));
        classList.addAll(InstanceFactory.getAllClassMap().get(Monitor.class.getSimpleName()));

        for(Iterator<Class<?>> it = classList.iterator(); it.hasNext();) {
            Class<?> clazz = it.next();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                field.getAnnotation(Import.class);
            }
        }

    }

}
