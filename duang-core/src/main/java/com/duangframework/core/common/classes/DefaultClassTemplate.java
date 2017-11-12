package com.duangframework.core.common.classes;

import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laotang on 2017/11/12 0012.
 */
public class DefaultClassTemplate extends AbstractClassTemplate {

    private static Map<String, Class<?>> annotationMap = new HashMap<>();
    static {
        annotationMap.put(Controller.class.getName(), Controller.class);
        annotationMap.put(Service.class.getName(), Service.class);
    }

    public DefaultClassTemplate() {
        super(annotationMap);
    }

    public DefaultClassTemplate(Map<String, Class<?>> annotationMap) {
        super(annotationMap);
    }
}
