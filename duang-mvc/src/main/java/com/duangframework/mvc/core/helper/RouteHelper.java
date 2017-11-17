package com.duangframework.mvc.core.helper;

import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Mapping;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mvc.core.Action;
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

    public static void duang() {
        Map<String, Object> controllerMap = InstanceFactory.getAllBeanMaps().get(CONTROLLER_ENDWITH_NAME);
        if(ToolsKit.isEmpty(controllerMap)) {
            throw new EmptyNullException("mvc controller is null");
        }
        Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();
        // 遍历所有Controller对象，取出Mapping注解，生成路由集合
        for(Iterator<Map.Entry<String, Object>> it = controllerMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = it.next();
            Class<?> controllerClass = entry.getValue().getClass();
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
                    if(!ToolsKit.isExist(actionKey, actionMapping)){
                        actionMapping.put(actionKey, action);
                    }
                }
            }
        }

        List<String> keyList = getAllActionKeys();
        logger.warn("**************** All BaseController Mapper Key ****************");
        for (String key : keyList) {
            logger.warn(key);
        }
        if(!actionMapping.isEmpty()){
            InstanceFactory.setActionMapping(actionMapping);
        }
        logger.warn("RouteHelper Success...");
    }

    private static Action buildAction(Class<?> controllerClass, String controllerKey, Mapping mapping, Method method) {
        String methodName = method.getName();
        String methodKey = buildMappingKey(mapping, methodName);
        String actionKey = methodKey;
        if(!controllerKey.equalsIgnoreCase(methodKey)) {
            actionKey = controllerKey +"/"+ methodKey;
        }
        actionKey = actionKey.startsWith("/") ? actionKey : "/"+actionKey;
        String descKey = methodName, levelKey = "", orderKey = "";
        if(ToolsKit.isNotEmpty(mapping)) {
            descKey = ToolsKit.isEmpty(mapping.desc()) ? methodName : mapping.desc();
            levelKey = mapping.level()+"";
            orderKey = mapping.order();
        }
        return  new Action(actionKey, descKey, levelKey, orderKey, controllerClass, method);
    }

    private static String buildMappingKey(Mapping mapping, String mappingKey) {
        if(ToolsKit.isEmpty(mapping)) {
            if(mappingKey.endsWith(CONTROLLER_ENDWITH_NAME)) {
                mappingKey = mappingKey.replace(CONTROLLER_ENDWITH_NAME, "");
            }
        } else {
            mappingKey = mapping.value();
        }
        return mappingKey.endsWith("/") ? mappingKey.substring(0, mappingKey.length()-1).toLowerCase() : mappingKey.toLowerCase();
    }

    private static List<String> getAllActionKeys() {
        List<String> allActionKeys = new ArrayList<String>(actionMapping.keySet());
        Collections.sort(allActionKeys);
        return allActionKeys;
    }
}
