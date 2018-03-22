package com.duangframework.mysql.common;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/3/22.
 */
public interface IMySql {

    /**
     *
     * @return
     * @throws Exception
     */
    List<MySqlConnect> builderConnects() throws Exception;
}
