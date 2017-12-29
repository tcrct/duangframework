package com.duangframework.core.common.classes;

import com.duangframework.core.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author laotang
 * @date 2017/11/12 0012
 */
public class CustomizeClassTemplate extends AbstractClassTemplate {

    public CustomizeClassTemplate(Class<? extends Annotation> annotation,
                                  String packages,
                                  String jarName,
                                  String suffix) {
        super(annotation, packages, jarName, suffix);
    }

    public CustomizeClassTemplate(Set<Class<? extends Annotation>> annotationSet,
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
        for(Iterator<Class<? extends Annotation>> it = annotationSet.iterator(); it.hasNext();) {
            Class<? extends Annotation> annotClass = it.next();
            if (clazz.isAnnotationPresent(annotClass) && !filelist.contains(clazz)) {
                filelist.add(clazz);
            }
        }

    }

    @Override
    public Map<String, List<Class<?>>> getMap() throws Exception {
        return null;
    }
}
