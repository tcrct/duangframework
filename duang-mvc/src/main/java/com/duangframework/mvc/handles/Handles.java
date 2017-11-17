package com.duangframework.mvc.handles;

import com.duangframework.core.IHandle;
import com.duangframework.core.common.dto.http.request.HttpRequest;
import com.duangframework.core.common.dto.http.response.HttpResponse;
import com.duangframework.mvc.core.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 *
 * @author laotang
 * @date 2017/11/8
 */
public class Handles {

    private static Logger logger = LoggerFactory.getLogger(Handles.class);

    public static void init() {
//        InstanceFactory.getHandles().add(0, new InitHandle());
        // 添加ActionHandle，在其它的Handle后
        InstanceFactory.getHandles().add(new ActionHandle());

        printHandles();
    }

    private static void printHandles() {
        for (IHandle handle : InstanceFactory.getHandles()) {
            logger.warn(handle.getClass().getName() + " start success...");
        }
    }

    /**
     * 将存放在InstanceFactory.getHandles()里的所有Handle按顺序执行
     * @param target            请求URI
     * @param request          请求对象
     * @param response        返回对象
     * @throws Exception
     */
    public static void execute(String target, HttpRequest request, HttpResponse response) throws Exception {
        for (Iterator<IHandle> it = InstanceFactory.getHandles().iterator(); it.hasNext();) {
            it.next().execute(target, request, response);
        }
    }
}
