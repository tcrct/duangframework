package com.duangframework.mvc.handles;


import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.core.interfaces.IHandle;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mvc.core.Action;
import com.duangframework.mvc.core.BaseController;
import com.duangframework.mvc.core.InstanceFactory;
import com.duangframework.mvc.core.helper.IocHelper;

import java.lang.reflect.Method;

/**
 * @author Created by laotang
 * @date on 2017/11/17.
 */
public class ActionHandle implements IHandle {

    private static final Object[] NULL_ARGS = new Object[0];

    @Override
    public void execute(String target, HttpRequest request, HttpResponse response) throws Exception {
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
        // 根据URI取出对应的Action对象
        Action action = InstanceFactory.getActionMapping().get(target);
        if(action == null){
//            returnErrorJson(1, "action is null or access denied", request, response);
            return;
        }

        Class<?> controllerClass = action.getControllerClass();
        BaseController controller = null;
        //是否单例
        if(action.isSingleton()){
            controller = (BaseController) InstanceFactory.getAllBeanMap().get(action.getBeanKey());
        } else {
            // 如果不是设置为单例模式的话就每次请求都创建一个新的Controller对象
            controller = ClassUtils.newInstance(controllerClass);
            // 还要重新执行Ioc注入
            IocHelper.ioc(controller);
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

}
