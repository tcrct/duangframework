package com.duangframework.mvc.core.helper;

import com.duangframework.core.exceptions.MvcStartUpException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.kit.ClassScanKit;
import com.duangframework.mvc.proxy.AbstractProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/16.
 */
public class BeanHelper {

    private static Logger logger = LoggerFactory.getLogger(BeanHelper.class);

    public static void duang() {
        //扫描指定包路径下的类文件，类文件包含有指定的注解类或文件名以指定的字符串结尾的
        Map<String, List<Class<?>>> classMap =  ClassScanKit.duang()
                .annotations(InstanceFactory.MVC_ANNOTATION_SET)
                .packages("com.syt.qingbean")
                .jarname("qingbean")
                // 增加MVC固定扫描的包路径
                .packages(AbstractProxy.class.getPackage().getName())
                .map();
        // 将扫描后的Class进行实例化并缓存
        if(ToolsKit.isNotEmpty(classMap)) {
            try {
                for(Iterator<Map.Entry<String, List<Class<?>>>> it = classMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, List<Class<?>>> entry = it.next();
                    String key = entry.getKey();
                    List<Class<?>> classList = entry.getValue();
                    if(ToolsKit.isEmpty(classList)) { continue; }
                    Map<String,Object> subBeanMap = InstanceFactory.getAllBeanMaps().get(key);
                    if(ToolsKit.isEmpty(subBeanMap)) { subBeanMap = new HashMap<>(classList.size());}
                    for(Class<?> cls : classList) {
                        Object clsObj = ClassUtils.newInstance(cls);
                        // 实例化后，用类全名作key， 实例化对象作value缓存起来
                        subBeanMap.put(cls.getCanonicalName(), clsObj);
                    }
                    // 缓存到实例工厂
                    InstanceFactory.setAllBeanMaps(key, subBeanMap);
                }
            } catch (Exception e) {
                throw new MvcStartUpException(e.getMessage(), e);
            }
        }
        logger.warn("BeanHelper Success...");
    }
}
