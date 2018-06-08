package com.duangframework.mvc.core;

import com.alibaba.fastjson.annotation.JSONField;
import com.duangframework.core.annotation.mvc.Controller;
import com.duangframework.core.annotation.validation.Validation;
import com.duangframework.core.common.Const;
import com.duangframework.core.kit.ToolsKit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private int order;
	private Class<?> controllerClass;
	@JSONField(serialize=false, deserialize = false)
	private Method method;
	private String requestMethod;
	private String restfulKey;  //restful风格URI
	private long timeout = Const.REQUEST_TIMEOUT;  //请求过期时间
	private List<ValidationParam> paramList;

	public Action() {

	}

	public Action(String controllerKey, String actionKey, String desc, int level, int order,
                  Class<?> controllerClass, Method method, long timeout) {
	    this.controllerKey = controllerKey;
		this.actionKey = actionKey;
		this.desc = desc;
		this.level = level;
		this.order = order;
		this.controllerClass = controllerClass;
		this.method = method;
		this.timeout = timeout;
		getRequestMethod();
        getValidationParam();
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

	public int getOrder() {
		return order;
	}

	public long getTimeout() {
		return timeout;
	}

	public String getRequestMethod() {
		if(null == method) {
			return "";
		}
		if(ToolsKit.isEmpty(requestMethod)) {
			com.duangframework.core.annotation.mvc.Mapping methodMapping = method.getAnnotation(com.duangframework.core.annotation.mvc.Mapping.class);
            if (ToolsKit.isEmpty(methodMapping) || ToolsKit.isEmpty(methodMapping.method())) {
                return "";
            }
            StringBuilder resultBuilder = new StringBuilder();
			for (com.duangframework.core.annotation.mvc.Method method : methodMapping.method()) {
				resultBuilder.append(method.name()).append(",");
			}
			if (resultBuilder.length() > 1) {
				resultBuilder.deleteCharAt(resultBuilder.length() - 1);
			}
			requestMethod = resultBuilder.toString();
		}
		return requestMethod;
	}

	public List<ValidationParam> getValidationParam() {
		if(null == paramList && null != method) {
			com.duangframework.core.annotation.mvc.Mapping methodMapping = method.getAnnotation(com.duangframework.core.annotation.mvc.Mapping.class);
			if(ToolsKit.isNotEmpty(methodMapping)) {
				Validation[] paramArray = methodMapping.vtor();
				if(ToolsKit.isNotEmpty(paramArray)) {
                    paramList = new ArrayList<>(paramArray.length);
					for (Validation validation : paramArray) {
						ValidationParam validationParam = null;
                        Class<?> vtorClass = validation.bean();
						if(null != vtorClass && !Object.class.equals(vtorClass)){
//                            validationParam =
						}
                        validationParam = validationParamValue(validation);
                        paramList.add(validationParam);
					}
				}
			}
		}
		return paramList;
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

	@JSONField(serialize = false, deserialize = false)
    public Action getControllerAction() {
        controllerKey = "/"+controllerClass.getSimpleName().replace(Controller.class.getSimpleName(), "").toLowerCase();
        String desc = controllerKey;
        int level = 0;
        int order = 0;
        com.duangframework.core.annotation.mvc.Mapping controllerMapping = getControllerClass().getAnnotation(com.duangframework.core.annotation.mvc.Mapping.class);
        if(ToolsKit.isNotEmpty(controllerMapping)) {
            controllerKey = ToolsKit.isEmpty(controllerMapping.value()) ? controllerKey : controllerMapping.value().toLowerCase();
            desc = ToolsKit.isNotEmpty(controllerMapping.desc()) ? controllerMapping.desc() : controllerKey;
            level = ToolsKit.isNotEmpty(controllerMapping.level()) ? controllerMapping.level() : level;
            order = ToolsKit.isNotEmpty(controllerMapping.order()) ? controllerMapping.order() : order;
        }
        return new Action(controllerKey, "", desc, level, order, controllerClass, null, 0);
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

    /**
     * 验证参数值
     * @param validation
     * @return
     */
	private ValidationParam validationParamValue(Validation validation) {
        ValidationParam validationParam = new ValidationParam(validation.isEmpty(), validation.length(), validation.range(),
                validation.fieldName(), validation.fieldValue(), validation.desc(), validation.formatDate(),
                validation.oid(), validation.fieldType(), validation.bean());

        //默认值的设置为null，不返回到客户端
        if(Object.class.equals(validationParam.getBeanClass())) {
            validationParam.setBeanClass(null);
        }
        if(!Date.class.equals(validationParam.getTypeClass())){
            validationParam.setFormatDate(null);
        }
        if(validationParam.getLength() ==0){
            validationParam.setLength(null);
        }

        return validationParam;
    }
}
