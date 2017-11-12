package com.duangframework.core.common.classes;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 类扫描抽象类
 * @author laotang
 * @date 2017/11/12 0012
 */
public abstract class AbstractClassTemplate<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClassTemplate.class);

    protected Map<String, Class<?>> annotationMap = new HashMap<>();
    protected Set<String> packageSet = new HashSet<>();
    protected Set<String> jarNameSet = new HashSet<>();
    protected Set<String> suffixSet  = new HashSet<>();

    protected AbstractClassTemplate(Map<String, Class<?>> annotationMap,
                                    Set<String> packageSet,
                                    Set<String> jarNameSet,
                                    Set<String> suffixSet) {
        this.annotationMap = annotationMap;
        this.packageSet = packageSet;
        this.jarNameSet = jarNameSet;
        this.suffixSet = suffixSet;
    }

    protected AbstractClassTemplate(Map<String, Class<?>> annotationMap) {
        this(annotationMap, null, null, null);
    }

    protected AbstractClassTemplate(Map<String, Class<?>> annotationMap, Set<String> packageSet) {
        this(annotationMap, packageSet, null, null);
    }

    /**
    替换重复的包路径
     */
    private void replaceDuplicatePackageName() {
        if (ToolsKit.isEmpty(packageSet) ) {
           throw new EmptyNullException("包路径为空，请指定包路径");
        }

        Set<String> tmpPackageSet = new HashSet<>(packageSet.size());

        String[] packageArray = packageSet.toArray(new String[packageSet.size()]);

        Arrays.sort(packageArray, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o1.length() > o2.length()) ? 0 : 1;
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        });

        for (Iterator<String> it = packageSet.iterator(); it.hasNext();) {
            String key = it.next();
            for(String packageName : packageArray) {
                if (key.startsWith(packageName)) {

                }
            }
        }
    }

    public final List<T> getList() throws IOException{
        String packageName = "";
        Enumeration<URL> urls = ClassUtils.getClassLoader().getResources(packageName.replace(".", "/"));
        return null;
    }

}
