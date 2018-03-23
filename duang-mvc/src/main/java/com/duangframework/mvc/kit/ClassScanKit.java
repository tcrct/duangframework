package com.duangframework.mvc.kit;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.common.classes.DefaultClassTemplate;
import com.duangframework.core.common.classes.IClassTemplate;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类扫描工具类
 * @author laotang
 * @date 2017/11/11 0011
 */
public class ClassScanKit {

    private static Logger logger = LoggerFactory.getLogger(ClassScanKit.class);

    private static ClassScanKit _classScanKit;
    private static Lock _classScanLock = new ReentrantLock();
    private static Set<Class<? extends Annotation>> annotationSet = new HashSet<>();
    private static Set<String> packageSet = new HashSet<>();
    private static Set<String> jarNameSet = new HashSet<>();
    private static Set<String> suffixSet  = new HashSet<>();
    private static IClassTemplate template;

    public static ClassScanKit duang() {
        if(null == _classScanKit) {
            try {
                _classScanLock.lock();
                _classScanKit = new ClassScanKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _classScanLock.unlock();
            }
        }
        clear();
        return _classScanKit;
    }

    private static void clear() {
        annotationSet.clear();
        packageSet.clear();
        jarNameSet.clear();
        suffixSet.clear();
        template = null;
    }

    /**
     * 添加要扫描的类文件包含的注解class, 若有多个时，重复调用
     * @return
     */
    public ClassScanKit annotation(Class<? extends Annotation> annotClass) {
        annotationSet.add(annotClass);
        return _classScanKit;
    }

    public ClassScanKit annotations(Set<Class<? extends Annotation>> annotClassSet) {
        annotationSet.addAll(annotClassSet);
        return _classScanKit;
    }

    /**
     * 添加要扫描类文件的包路径，若有多个时，重复调用
     * @return
     */
    public ClassScanKit packages(String[] packageName) {
        packageSet.addAll(Arrays.asList(packageName));
        return _classScanKit;
    }

    public ClassScanKit packages(String packageName) {
        packageSet.add(packageName);
        return _classScanKit;
    }

    /**
     * 添加要扫描类文件的所在的jar文件名，若有多个时，重复调用
     * @return
     */
    public ClassScanKit jarname(String[] jarName) {
        jarNameSet.addAll(Arrays.asList(jarName));
        return _classScanKit;
    }

    public ClassScanKit jarname(String jarName) {
        jarNameSet.add(jarName);
        return _classScanKit;
    }

    /**
     * 添加要扫描类文件的后缀标识，若有多个时，重复调用
     * 即以什么字符串结尾的，
     * 如MainController.java时，这里可以输入Controller
     * 如MainService.java时，这里可以输入Service
     * 兼容Duang3.0以前的扫描规则
     * @return
     */
    public ClassScanKit suffix(String suffix) {
        suffixSet.add(suffix);
        return _classScanKit;
    }

    /**
     * 设置自定义模板
     * @param classTemplate 自定义的模板，实现@IClassTemplate
     * @return
     */
    public ClassScanKit template(IClassTemplate classTemplate) {
        template = classTemplate;
        return _classScanKit;
    }

    private void checkClassTemplate() {
        // 兼容Duang2.0版前的规则
        if(!annotationSet.isEmpty()) {
            for(Iterator<Class<? extends Annotation>> it = annotationSet.iterator(); it.hasNext();) {
                Class<? extends Annotation> annotationClass = it.next();
                //过滤代理类以Proxy结尾的类
                if(annotationClass.equals(Proxy.class)) {
                    continue;
                }
                suffixSet.add(annotationClass.getSimpleName());
            }
        }
        if(ToolsKit.isEmpty(template)) {
            template = new DefaultClassTemplate(annotationSet, packageSet, jarNameSet, suffixSet);
        }
    }

    /**
     *
     * @return
     */
    public List<Class<?>> list() {
        try {
            checkClassTemplate();
            return template.getList();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * @return
     */
    public Map<String, List<Class<?>>> map() {
        try {
            checkClassTemplate();
            return template.getMap();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

}
