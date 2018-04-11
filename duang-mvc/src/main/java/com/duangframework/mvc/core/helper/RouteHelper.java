package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Mapping;
import com.duangframework.core.common.Const;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Created by laotang
 * @date on 2017/11/17.
 */
public class RouteHelper {

    private static Logger logger = LoggerFactory.getLogger(RouteHelper.class);

    public final static String CONTROLLER_ENDWITH_NAME = Controller.class.getSimpleName();
    private static Map<String, Action> actionMapping = new HashMap<String, Action>();
    private static Map<String, Action> restfulActionMapping = new HashMap<String, Action>();

    public static void duang() {
        Map<Class<?>, Object> controllerMap = BeanUtils.getAllBeanMaps().get(CONTROLLER_ENDWITH_NAME);
        if(ToolsKit.isEmpty(controllerMap)) {
            throw new EmptyNullException("mvc controller is null");
        }
        Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();
        Method[] baseControllerMethods = BaseController.class.getMethods();
        for(Method method : baseControllerMethods) {
            excludedMethodName.add(method.getName());
        }
        // 遍历所有Controller对象，取出Mapping注解，生成路由集合
        for(Iterator<Map.Entry<Class<?>, Object>> it = controllerMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class<?>, Object> entry = it.next();
            Class<?> controllerClass = entry.getKey();
            Mapping controllerMapping = controllerClass.getAnnotation(Mapping.class);
//            String controllerKey = ToolsKit.isEmpty(controllerMapping) ? buildMappingKey(controllerClass.getSimpleName()) : controllerMapping.value().toLowerCase();
            String controllerKey = buildMappingKey(controllerMapping, controllerClass.getSimpleName());
            for(Method method : controllerClass.getMethods()) {
               Mapping methodMapping = method.getAnnotation(Mapping.class);
               String methodName = method.getName();
                //Object类公用 方法名并且没有参数的方法
                if(!excludedMethodName.contains(methodName) && method.getParameterTypes().length ==0 ) {
                    Action action = buildAction(controllerClass, controllerKey, methodMapping, method);
                    String actionKey = action.getActionKey();
                    if (actionKey.contains("{") && actionKey.contains("}")) {
                        action.setRestfulKey(actionKey);
                        restfulActionMapping.put(actionKey, action);
                    }
                    if(!ToolsKit.isExist(actionKey, actionMapping)){
                        actionMapping.put(actionKey, action);
                    }
                }
            }
        }

        List<String> keyList = getAllActionKeys();
        logger.warn("**************** Controller Mapper Key ****************");
        for (String key : keyList) {
            if(key.contains(Const.REPORT_MAPPING_KEY)) {
                continue;
            }
            logger.warn(key);
        }
        if(!actionMapping.isEmpty()){
            InstanceFactory.setActionMapping(actionMapping);
        }
        if(!restfulActionMapping.isEmpty()){
            InstanceFactory.setRestfulActionMapping(restfulActionMapping);
        }
        logger.warn("RouteHelper Success...");
    }

    private static Action buildAction(Class<?> controllerClass, String controllerKey, Mapping mapping, Method method) {
        String methodName = method.getName();
        String methodKey = buildMappingKey(mapping, methodName);
        String actionKey = methodKey;
        long timeout = Const.REQUEST_TIMEOUT;
        if(!controllerKey.equalsIgnoreCase(methodKey)) {
            actionKey = controllerKey + (methodKey.startsWith("/") ? methodKey : "/"+ methodKey);
        }
        actionKey = actionKey.startsWith("/") ? actionKey : "/"+actionKey;
        String desc = methodName;
        int level = 0, order =0;
        if(ToolsKit.isNotEmpty(mapping)) {
            desc = ToolsKit.isEmpty(mapping.desc()) ? methodName : mapping.desc();
            level = mapping.level();
            order = mapping.order();
            timeout = mapping.timeout();
        }
        return  new Action(controllerKey, actionKey, desc, level, order, controllerClass, method, timeout);
    }

    private static String buildMappingKey(Mapping mapping, String mappingKey) {

        if(ToolsKit.isNotEmpty(mapping) && ToolsKit.isNotEmpty(mapping.value())) {
            mappingKey = mapping.value();
        } else {
            if(mappingKey.endsWith(CONTROLLER_ENDWITH_NAME)) {
                mappingKey = mappingKey.replace(CONTROLLER_ENDWITH_NAME, "");
            }
        }
        mappingKey = mappingKey.startsWith("/") ? mappingKey : "/"+ mappingKey;
        return mappingKey.endsWith("/") ? mappingKey.substring(0, mappingKey.length()-1).toLowerCase() : mappingKey.toLowerCase();
    }

    private static List<String> getAllActionKeys() {
        List<String> allActionKeys = new ArrayList<>(actionMapping.keySet());
        Collections.sort(allActionKeys);
        return allActionKeys;
    }
}
