package com.duangframework.mvc.handles;


import com.duangframework.core.common.dto.http.request.IRequest;
import com.duangframework.core.common.dto.http.response.IResponse;
import com.duangframework.core.exceptions.DuangMvcException;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.core.helper.IocHelper;
import com.duangframework.server.common.enums.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Created by laotang
 * @date on 2017/11/17.
 */
public class ActionHandle implements IHandle {

    private static final Object[] NULL_ARGS = new Object[0];

    @Override
    public void execute(String target, IRequest request, IResponse response) throws Exception {

        if(HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            return;
        }

        // 请求的URL中如果包含有.  则全部当作是静态文件的请求处理，直接返回
        if (target.contains(".")) { return; }
        // 分号后的字符截断
        if(target.contains(";")){
            target = target.substring(0,target.indexOf(";"));
        }
        // 暂不支持根目录请求
        if("/".equals(target)) {
            return;
        }

        Action action = InstanceFactory.getActionMapping().get(target);

        if(null == action){
            action = getRestfulActionMapping(request, target);
            if(null == action) {
                throw new DuangMvcException("action is null or access denied");
            }
        }

        Class<?> controllerClass = action.getControllerClass();
        BaseController controller = null;
        //是否单例
        if(action.isSingleton()){
            controller = (BaseController) BeanUtils.getBean(action.getControllerClass(), controllerClass);
        } else {
            // 如果不是设置为单例模式的话就每次请求都创建一个新的Controller对象
            controller = ClassUtils.newInstance(controllerClass);
            // 还要重新执行Ioc注入
            IocHelper.ioc(controller.getClass());
        }
        // 传入request, response到Controller
        controller.init(request, response);
        // 取出方法对象
        Method method = action.getMethod();
        // 取消类型安全检测（可提高反射性能）
        method.setAccessible(true);
        // 反射执行方法
        method.invoke(controller, NULL_ARGS);
        // 返回结果
        controller.getRender().setContext(request, response).render();
    }

    /**
     * 根据restful风格URI，取出对应的Action
     * @param request          请求对象
     * @param target            请求URI
     * @return
     */
    private Action getRestfulActionMapping(IRequest request, String target) {
        Map<String,String> paramMap = new HashMap<>();
        Action action = null;
        for(Iterator<Map.Entry<String,Action>> iterator = InstanceFactory.getRestfulActionMapping().entrySet().iterator(); iterator.hasNext();) {
            int index = 0;
            paramMap.clear();
            Map.Entry<String,Action> entry = iterator.next();
            String key = entry.getKey();
            // action对象里的URI根据/分裂成数组
            String[] actionKeyArray = key.split("\\/");
            // 请求URI根据/分裂成数组
            String[] targetKeyArray = target.split("\\/");
            // 长度不等则直接退出本次遍历
            if(actionKeyArray.length != targetKeyArray.length) {
                continue;
            }
            int actionKeyLen = actionKeyArray.length;
            for(int i=0; i<actionKeyLen; i++) {
                if(actionKeyArray[i].equals(targetKeyArray[i])) {
                    index++;
                } else if(actionKeyArray[i].startsWith("{") && actionKeyArray[i].endsWith("}")) {
                    // 去掉{}后，得出请求name
                    String paramName = actionKeyArray[i].substring(1, actionKeyArray[i].length()-1);
                    // 设置到Map里
                    paramMap.put(paramName, targetKeyArray[i]);
                    index++;
                }
            }
            // 如果匹配的长度一致，则设置这个target对应的Action对象并退出循环
            if(index == actionKeyLen) {
                action = entry.getValue();
                break;
            }
        }
        // 设置到request里
        if(ToolsKit.isNotEmpty(action) && !paramMap.isEmpty()) {
            for(Iterator<Map.Entry<String,String>> iterator = paramMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return action;
    }

}
