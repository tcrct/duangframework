package com.duangframework.mvc.core;

import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Method;

/**
 *
 * @author laotang
 * @
 */
public class Action {

	private String actionKey;
	private String descKey;
	private String levelKey;
	private String orderKey;
	private Class<?> controllerClass;
	private Method method;
	private String restfulKey;  //restful风格URI

	public Action() {

	}

	public Action(String actionKey, String descKey, String levelKey, String orderKey,
                  Class<?> controllerClass, Method method) {
		this.actionKey = actionKey;
		this.descKey = descKey;
		this.levelKey = levelKey;
		this.orderKey = orderKey;
		this.controllerClass = controllerClass;
		this.method = method;
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

	public String getDescKey() {
		return descKey;
	}

	public String getLevelKey() {
		return levelKey;
	}

	public String getOrderKey() {
		return orderKey;
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
