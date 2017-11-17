package com.duangframework.mvc.proxy;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.aop.Tx;
import com.duangframework.core.common.aop.ProxyChain;
import com.duangframework.core.interfaces.IProxy;

/**
 *
 * @author Created by laotang
 * @date on 2017/11/16.
 */
@Proxy(aop = Tx.class)
public class TxProxy implements IProxy {
    @Override
    public Object doProxy(ProxyChain proxyChain) throws Exception {
        System.out.println("##########TxProxy Method: " + proxyChain.getTargetMethod().getName());
        return proxyChain.doProxyChain();
    }
}
