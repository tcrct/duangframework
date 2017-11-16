package com.duangframework.mvc.proxy;

import com.duangframework.core.annotation.aop.Proxy;
import com.duangframework.core.annotation.aop.Tx;
import com.duangframework.mvc.core.IProxy;

/**
 *
 * @author Created by laotang
 * @date on 2017/11/16.
 */
@Proxy(aop = Tx.class)
public class TxProxy implements IProxy {
}
