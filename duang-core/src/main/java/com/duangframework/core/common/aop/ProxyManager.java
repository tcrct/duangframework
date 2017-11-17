package com.duangframework.core.common.aop;

import com.duangframework.core.interfaces.IProxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理管理器
 */
public class ProxyManager {

	private static final Logger logger = LoggerFactory.getLogger(ProxyManager.class);

	/**
	 * 创建代理实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createProxy(final Class<?> targetClass, final List<IProxy> proxyList) {
		return (T) Enhancer.create(targetClass, new MethodInterceptor() {
			@Override
			public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
					MethodProxy methodProxy) throws Throwable {
				return new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList)
						.doProxyChain();
			}
		});
	}
}