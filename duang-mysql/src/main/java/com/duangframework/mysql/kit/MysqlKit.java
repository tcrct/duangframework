package com.duangframework.mysql.kit;

import com.duangframework.core.common.dto.result.PageDto;
import com.duangframework.mysql.core.DBSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlKit {

    private static final Logger logger = LoggerFactory.getLogger(MysqlKit.class);
    private static MysqlKit _mysqlKit;
    private static Lock _mysqlKitLock = new ReentrantLock();
    private String _executeSql;
    private Object[] _params;

    public static MysqlKit duang() {
        if(null == _mysqlKit) {
            try {
                _mysqlKitLock.lock();
                _mysqlKit = new MysqlKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _mysqlKitLock.unlock();
            }
        }
        clear();
        return _mysqlKit;
    }

    private static void clear() {

    }

    public MysqlKit sql(String sql) {
        this._executeSql = sql;
        return this;
    }

    public MysqlKit params(Object... params) {
        this._params = params;
        return this;
    }



    public <T> T first() {
        DBSession.execute(_executeSql, _params);
        return null;
    }

    public <T> List<T> findList() {
        return null;
    }

    public <T> PageDto<T> findPage() {
        return null;
    }


}
