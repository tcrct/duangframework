package com.duangframework.ext.utils;

import com.duangframework.core.annotation.mvc.Mapping;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.ext.report.dto.ActionInfoDto;
import com.duangframework.mvc.core.Action;

import java.lang.reflect.Method;

/**
 * @author Created by laotang
 * @date createed in 2018/5/30.
 */
public class ReportUtils {


    /**
     * private String controllerKey;   // controller的映射URI
     private String actionKey;// api的映射URI
     private String desc;// 该api功能简要说明
     private int level;// api在树型结构下的等级
     private int order;// api在同等级下的排序
     private String controllerName;// api的controller的类名称
     private String methodName;// api的请求 method的名称(get, post, put, delete)
     private ValidationParam mappingInfoDto; // @Mapping注解里的@Param注解对象
     private String restfulKey;  //restful风格映射URI
     private long timeout;  //请求过期时间
     * @param source
     * @param target
     */
    public static void  conversionDto(Action source, ActionInfoDto target) {
        if(ToolsKit.isEmpty(source) || ToolsKit.isEmpty(target)) {
            throw new EmptyNullException("转换对象不能为null");
        }
        target.setControllerKey(source.getControllerKey());
        target.setActionKey(source.getActionKey());
        target.setDesc(source.getDesc());
        target.setLevel(source.getLevel());
        target.setOrder(source.getOrder());
        target.setRestfulKey(source.getRestfulKey());
        target.setTimeout(source.getTimeout());
        if (ToolsKit.isNotEmpty(source.getControllerClass())) {
            target.setControllerName(source.getControllerClass().getName());
        }
        Method method = source.getMethod();
        if (ToolsKit.isNotEmpty(method)) {
            target.setMethodName(method.getName());
            Mapping mapping = method.getAnnotation(Mapping.class);
            if(ToolsKit.isNotEmpty(mapping)) {
//             mapping.params();
            }
        }
    }

}
