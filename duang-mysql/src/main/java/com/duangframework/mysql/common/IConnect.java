package com.duangframework.mysql.common;

/**
 *
 * @author laotang
 * @date 2017/11/25 0025
 */
public interface IConnect {

    /**
     * 用户名
     * @return
     */
    String getUserName();

    /**
     * 密码
     * @return
     */
    String getPassWord();

    /**
     * 链接字符串
     * @return
     */
    String getJdbcUrl();
}
