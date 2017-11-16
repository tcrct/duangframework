package com.duangframework.core.common;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.db.Entity;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.mvc.Service;


/**
 *  常量类
 * @author laotang
 * @date 2017/11/15
 */
public class Const {

    public final static String CONTROLLER_ENDWITH_NAME = Controller.class.getSimpleName();
    public final static String SERVICE_ENDWITH_NAME = Service.class.getSimpleName();
    public final static String PROXY_ENDWITH_NAME = Proxy.class.getSimpleName();
    public final static String ENTITY_ENDWITH_NAME = Entity.class.getSimpleName();
}
