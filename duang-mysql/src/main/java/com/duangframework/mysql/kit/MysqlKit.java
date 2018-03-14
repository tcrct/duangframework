package com.duangframework.mysql.kit;

import com.duangframework.mysql.core.DBSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlKit {

    private static final Logger logger = LoggerFactory.getLogger(MysqlKit.class);
    private static MysqlKit _mysqlKit;
    private static Lock _mysqlKitLock = new ReentrantLock();
    private String _executeSql = "";
    private Object[] _params = null;
    private List<Map<String,Object>> resultList = new ArrayList<>();

    public static MysqlKit duang() {
        return new MysqlKit();
    }


    public MysqlKit sql(String sql) {
        this._executeSql = sql;
        return this;
    }

    public MysqlKit params(Object... params) {
        this._params = params;
        return this;
    }

    public List<Map<String,Object>> query() {
        try {
            resultList = DBSession.query(_executeSql, _params);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return resultList;
    }

    public boolean add() {
        return delete();
    }

    public boolean update() {
        return delete();
    }

    public boolean delete() {
        int executeResultCount = -1;
        try {
            executeResultCount = DBSession.execute(_executeSql, _params);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return (executeResultCount > -1) ? true : false;
    }

/*
    public <T> T asBean(Class<T> clazz) {
        if(resultList.isEmpty()) {
           return null;
        }


    }

    public <T> List<T> asBeanList() {
        return null;
    }

    public <T> PageDto<T> asPage() {
        return null;
    }
*/

}
