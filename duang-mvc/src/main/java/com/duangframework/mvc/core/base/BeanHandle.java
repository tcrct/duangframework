package com.duangframework.mvc.core.base;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.kit.ClassScanKit;

import java.util.*;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class BeanHandle {


    public static void duang() {
        //扫描指定包路径下的类文件，类文件包含有指定的注解类或文件名以指定的字符串结尾的
        Map<String, List<Class<?>>> classMap =  ClassScanKit.duang()
                .annotations(InstanceFactory.MVC_ANNOTATION_SET)
                .packages("com.syt.qingbean")
                .jarname("qingbean")
                .map();

        // 将扫描后的Class进行实例化并缓存
        if(ToolsKit.isNotEmpty(classMap)) {
            Map<String,Object> beanMap = new HashMap<>(classMap.size());
            for(Iterator<Map.Entry<String, List<Class<?>>>> it = classMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, List<Class<?>>> entry = it.next();
                List<Class<?>> classList = entry.getValue();
                if(ToolsKit.isEmpty(classList)) { continue; }
                for(Class<?> cls : classList) {
                    Class<?> beanClass = ClassUtils.loadClass(cls, true);
                    if(ToolsKit.isNotEmpty(beanClass)) {
                        try {
                            beanMap.put(beanClass.getCanonicalName(), (Object)beanClass.newInstance());
                        } catch (Exception e) {}
                    }
                }
            }
            InstanceFactory.setAllBeanMap(beanMap);
        }
    }

//    public static List<Class<?>> newInstance() {
//
//    }


}
