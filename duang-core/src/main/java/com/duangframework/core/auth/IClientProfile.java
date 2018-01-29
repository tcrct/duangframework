package com.duangframework.core.auth;

import com.duangframework.core.common.enums.FormatTypeEnums;
import com.duangframework.core.interfaces.ICredentials;

/**
 * @author Created by laotang
 * @date createed in 2018/1/27.
 */
public interface IClientProfile {

    /**
     *
     * @return
     */
    String getEndPoint();

    /**
     *  请求与返回参数格式
     * @return
     */
    FormatTypeEnums getFormat();

    /**
     * 证书
     * @return
     */
    ICredentials getCredential();
}
