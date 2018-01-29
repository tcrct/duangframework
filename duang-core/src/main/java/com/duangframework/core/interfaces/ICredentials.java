package com.duangframework.core.interfaces;

/**
 * @author Created by laotang
 * @date createed in 2018/1/27.
 */
public interface ICredentials {

    String APPID_FIELD = "appId";
    String APPSECRET_FIELD = "appSecret";
    /**
     * @return the Access Key Id for this credential
     */
    String getAccessKeyId();

    /**
     * @return the Access Key Secret for this credential
     */
    String getAccessKeySecret();
}
