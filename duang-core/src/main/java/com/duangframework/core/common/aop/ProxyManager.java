package com.duangframework.core.common.aop;

import com.duangframework.core.interfaces.IProxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.duang.config.Config;
import org.duang.config.InstanceFactory;
import org.duang.kit.*;
import org.duang.logs.Logger;
import org.duang.proxy.DuangProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * 代理管理器
 */
public class ProxyManager {

	private static final Logger logger = LogKit.getLogger(ProxyManager.class);

	private static final Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();
	
	private static int methodInvokeIndex=0;
	
	private static final Object[] NULL_ARGS = new Object[1];

	/**
	 * 创建代理实例
	 */
	@SuppressWarnings("unchecked")
	private static <T> T createProxy(final Class<?> targetClass, final List<IProxy> proxyList) {
		return (T) Enhancer.create(targetClass, new MethodInterceptor() {
			@Override
			public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
					MethodProxy methodProxy) throws Throwable {
				return new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList)
						.doProxyChain();
			}
		});
	}

	/**
	 * 通过反射创建代理实例
	 */
	public static <T> T newProxyInstance(Class<?> commandClass) {
		if(commandClass.isInterface()) { return null;}		//如果是接口的话，则直接返回null
		T instance = null;		
		try {
			Method[] methods = commandClass.getMethods();
			List<IProxy> proxyList = new ArrayList<IProxy>();
			// 如果开启了DRL,则添加DuangProxy的方法代理, 添到在集合的第一的位置上
			if(Config.isEnableDRL()) proxyList.add(new DuangProxy());
			for (Method method : methods) {
				if (excludedMethodName.contains(method.getName()))
					continue;
				for (Iterator<Entry<Class<? extends Annotation>, Class<? extends IProxy>>> it = InstanceFactory.getAnnotation().entrySet().iterator(); it.hasNext();) {
					Entry<Class<? extends Annotation>, Class<? extends IProxy>> entry = it.next();
					if (method.isAnnotationPresent(entry.getKey())) {
						proxyList.add((IProxy) ObjectKit.newInstance(entry.getValue()));
					}
				}
			}
			if (ToolsKit.isNotEmpty(proxyList)) {
				instance = createProxy(commandClass, proxyList);
				InstanceFactory.setProxyService(commandClass, proxyList);
			} else if (ToolsKit.isEmpty( instance)){
				 instance = ObjectKit.newInstance(commandClass);
			}
			return instance;
		} catch (Exception e) {
			logger.print("创建代理实例出错！", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 用线程方式初始化注解
	 * 仅对需要初始化的注解进行操作，如org.duang.ioc.Mq
	 */
	public static void initAnnotation() {
		try {
			final Map<Class<?>,List<IProxy>> proxyServiceMap = InstanceFactory.getProxyService();
			if (ToolsKit.isEmpty(proxyServiceMap)) return;
			final Map<Class<? extends Annotation>,Class<? extends IProxy>> annotationMap= InstanceFactory.getAnnotation();
			if (ToolsKit.isEmpty(annotationMap)) return;
			ThreadPoolKit.execute(new Thread() {
				@Override
				public void run() {
					for (Iterator<Entry<Class<?>,List<IProxy>>> it = proxyServiceMap.entrySet().iterator(); it.hasNext();) {
						Entry<Class<?>,List<IProxy>> entry = it.next();
						Class<?> cls = entry.getKey();
						Method[] methods = cls.getMethods();
						for (Method method : methods) {
							if (excludedMethodName.contains(method.getName())) continue;
							methodInvoke(method, cls);
						}
					}
					if(methodInvokeIndex > 0){
						logger.print("正在初始化代理完毕, 共代理 " + methodInvokeIndex + "个方法！" );
					}
				}
			});
		} catch (Exception e) {
			logger.print("用线程方式初始化注解时出错！", e);
		}
	}
	
	private static void methodInvoke(Method method, Class<?> cls){
		try {
			for (Iterator<Entry<Class<? extends Annotation>, Class<? extends IProxy>>> it = InstanceFactory.getAnnotation().entrySet().iterator(); it.hasNext();) {
				Entry<Class<? extends Annotation>, Class<? extends IProxy>> entry = it.next();
				Class<? extends Annotation> annotationClass = entry.getKey();
				Method[] annotationMethods = annotationClass.getMethods();
				boolean isInitAnnotation = false;
				if(ToolsKit.isEmpty(annotationMethods)) continue;
	    		for(Method annotationMethod : annotationMethods){
	    			if("init".equals(annotationMethod.getName()) && Boolean.parseBoolean(annotationMethod.getDefaultValue()+"")) {
	    				isInitAnnotation = true;
	    				break;
	    			}
	    		}
	    		if(!isInitAnnotation) continue;		//不需要初始化的就退出本次循环
				if (method.isAnnotationPresent(annotationClass)) { 
					method.invoke(BeanKit.getBean(cls), NULL_ARGS);
					System.out.println("正在初始化消息队列代理, 类名:  " + cls.getName()+"   方法名: "+ method.getName() +"   注解名: "+ entry.getKey().getName());
					methodInvokeIndex++;
				}
			}
			
		} catch (Exception e) {
			logger.print("利用反射机制执行注解方法时出错！", e);
		}
	}
}