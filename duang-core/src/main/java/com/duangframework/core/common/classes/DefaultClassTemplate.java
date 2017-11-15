package com.duangframework.core.common.classes;

import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Service;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 *
 * @author laotang
 * @date 2017/11/12 0012
 */
public class DefaultClassTemplate extends AbstractClassTemplate {

    private static Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
    static {
        annotationSet.add(Controller.class);
        annotationSet.add(Service.class);
    }

    public DefaultClassTemplate() {
        super(annotationSet);
    }

    public DefaultClassTemplate(Set<Class<? extends Annotation>> annotationSet,
                                Set<String> packageSet,
                                Set<String> jarNameSet,
                                Set<String> suffixSet) {
        super(annotationSet, packageSet, jarNameSet, suffixSet);
    }

    /**
     * 实现添加类方法
     * @param filelist      要存在的集合
     * @param packageName       包名
     * @param fileName                文件名
     * @param suffix                      扩展名
     */
    @Override
    public void doLoadClass(List<Class<?>> filelist, String packageName, String fileName, String suffix) {

        Class<?> clazz = ClassUtils.loadClass(packageName+"."+fileName, false);

        for (Iterator<String> it = suffixSet.iterator(); it.hasNext();) {
            String suffixStr = it.next();
            if(fileName.endsWith(suffixStr)) {
                filelist.add(clazz);
            }
        }

        for(Iterator<Class<? extends Annotation>> it = annotationSet.iterator(); it.hasNext();) {
            Class<? extends Annotation> annotClass = it.next();
            if (clazz.isAnnotationPresent(annotClass) && !filelist.contains(clazz)) {
                filelist.add(clazz);
            }
        }

    }

    @Override
    public Map<String, List<Class<?>>> getMap() throws Exception {
        List<Class<?>> classList = getList();
        if(ToolsKit.isEmpty(classList)) {
            return null;
        }
        Map<String, List<Class<?>>> classMap = new HashMap<>();
        for(Iterator<Class<?>> it = classList.iterator(); it.hasNext();) {
            Class<?> clazz = it.next();

//            for(String suffix : suffixSet) {
//                classMap
//            }

            for(Class<? extends  Annotation> annotClass : annotationSet) {
                if(clazz.isAnnotationPresent(annotClass)) {
                    String key = annotClass.getName();
                    if(classMap.containsKey(key) ) {
                        classMap.get(key).add(clazz);
                    } else {
                        List<Class<?>> list = new ArrayList<>();
                        list.add(clazz);
                        classMap.put(key, list);
                    }
                }
            }
        }
        return classMap;
    }

}
