package com.duangframework.core.common.classes;

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
//        System.out.println(packageName+"       "+fileName+"           "+clazz.getName());
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
            if(ToolsKit.isNotEmpty(clazz.getAnnotations())) {
                for(Class<? extends Annotation> annotationClass : annotationSet) {
                    if(clazz.isAnnotationPresent(annotationClass)) {
                        String key = annotationClass.getSimpleName();
                        addClass2Map4Key(classMap, key, clazz);
                        break;
                    }
                }
            } else {
                if(ToolsKit.isNotEmpty(suffixSet)) {
                    for (Iterator<String> suffixIt = suffixSet.iterator(); suffixIt.hasNext(); ) {
                        String key = clazz.getSimpleName();
                        String suffix = suffixIt.next();
                        if (key.endsWith(suffix)) {
                            addClass2Map4Key(classMap, suffix, clazz);
                            break;
                        }
                    }
                }
            }
        }
        return classMap;
    }

    /**
     * 添加类到Map集合中，根据key作区分
     * @param classMap
     * @param key
     * @param clazz
     */
    private void addClass2Map4Key(Map<String, List<Class<?>>> classMap, String key, Class<?> clazz) {
        List<Class<?>> list = null;
        if(classMap.containsKey(key) ) {
            list = classMap.get(key);
            if (!list.contains(clazz)) {
                list.add(clazz);
            }
        } else {
            list = new ArrayList<>();
            list.add(clazz);
        }
        classMap.put(key, list);
    }

}
