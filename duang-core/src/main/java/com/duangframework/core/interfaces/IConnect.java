package com.duangframework.core.interfaces;

/**
 *
 * @author laotang
 * @date 2017/11/25 0025
 */
public interface IConnect {

    String getHost();

    int getPort();

    String getDataBase();

    String getUserName();

    String getPassWord();

    String getUrl();

    String getClientCode();

    String getDataSourceFactoryClassName();

}
