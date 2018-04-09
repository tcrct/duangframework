package com.duangframework.mvc.core;

import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Method;

/**
 *
 * @author laotang
 * @
 */
public class Action {

    private String controllerKey;
	private String actionKey;
	private String desc;
	private int level;
	private String order;
	private Class<?> controllerClass;
	@JSONField(serialize=false, deserialize = false)
	private Method method;
	private String restfulKey;  //restful风格URI
	private long timeout = Const.REQUEST_TIMEOUT;  //请求过期时间

	public Action() {

	}

	public Action(String controllerKey, String actionKey, String desc, int level, String order,
                  Class<?> controllerClass, Method method, long timeout) {
	    this.controllerKey = controllerKey;
		this.actionKey = actionKey;
		this.desc = desc;
		this.level = level;
		this.order = order;
		this.controllerClass = controllerClass;
		this.method = method;
		this.timeout = timeout;
	}

    public String getControllerKey() {
        return controllerKey;
    }

	public Method getMethod() {
		return method;
	}

	public String getActionKey() {
		return actionKey;
	}

	public Class<?> getControllerClass() {
		return controllerClass;
	}

	public String getDesc() {
		return desc;
	}

	public int getLevel() {
		return level;
	}

	public String getOrder() {
		return order;
	}

	public long getTimeout() {
		return timeout;
	}

	/**
	 * 默认是单例的
	 * @return
	 */
	public boolean isSingleton() {
		com.duangframework.core.annotation.mvc.Controller controllerAnnotation = getControllerClass().getAnnotation(com.duangframework.core.annotation.mvc.Controller.class);
		if(ToolsKit.isNotEmpty(controllerAnnotation) &&
				"prototype".equalsIgnoreCase(controllerAnnotation.scope()) ) {
			return false;
		}
		return true;
	}

	public String getBeanKey() {
		return getControllerClass().getCanonicalName();
	}

	public String getRestfulKey() {
		return restfulKey;
	}

	public void setRestfulKey(String restfulKey) {
		this.restfulKey = restfulKey;
	}
}
