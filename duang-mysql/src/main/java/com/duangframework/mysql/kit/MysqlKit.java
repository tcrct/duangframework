package com.duangframework.mysql.kit;

import com.duangframework.core.common.dto.result.PageDto;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mysql.core.DBSession;
import com.duangframework.mysql.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
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
    private String _dataBase = "";
    private Class<?> _entityClass;
    private Object[] _params = null;
    private List<Map<String,Object>> resultList = new ArrayList<>();

    public static MysqlKit duang() {
        return new MysqlKit();
    }

    private MysqlKit() {
        _dataBase = MysqlUtils.getDefualDataBase();
    }


    public MysqlKit entityClass(Class<?> clazz) {
        this._entityClass = clazz;
        return this;
    }

    public MysqlKit use(String dataBase) {
        this._dataBase = dataBase;
        return this;
    }

    public MysqlKit sql(String sql) {
        this._executeSql = sql;
        return this;
    }

    public MysqlKit params(Object... params) {
        this._params = params;
        return this;
    }

    public List<Map<String,Object>> query() throws Exception {
        resultList = DBSession.query(_dataBase, _executeSql, _params);
        return resultList;
    }

    private long count() throws Exception {
        String entityName = ClassUtils.getEntityName(_entityClass);
        String whereSql = "";
        if(_executeSql.toLowerCase().startsWith("select")) {
            int index = _executeSql.indexOf("where");
            whereSql = (index > -1) ?_executeSql.substring(index, _executeSql.length()) : "";
        }
        String countSql = "select count(id) from " + entityName +" " + whereSql;
        long count = 0L;
        try {
            List<Map<String,Object>> results = DBSession.query(_dataBase, countSql, _params);
            if(ToolsKit.isNotEmpty(results)){
                Map<String, Object> result = results.get(0);
                for(Iterator<Map.Entry<String,Object>> it = result.entrySet().iterator(); it.hasNext();){
                    Map.Entry<String,Object> entry = it.next();
                    count = (long)entry.getValue();
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return count;
    }

    public int add() throws Exception {
        return DBSession.execute(_dataBase, _executeSql, _params);
    }

    public boolean update() throws Exception {
        return delete();
    }

    public boolean delete() throws Exception {
        int executeResultCount =  DBSession.execute(_dataBase, _executeSql, _params);
        return (executeResultCount > 0) ? true : false;
    }


    public <T> T findOne() throws Exception {
        query();
        if(ToolsKit.isEmpty(resultList) || ToolsKit.isEmpty(resultList.get(0))) {
           return null;
        }
        if(ToolsKit.isEmpty(_entityClass)) {
            throw new EmptyNullException("entity class is null");
        }
        return (T)ToolsKit.jsonParseObject(ToolsKit.toJsonString(resultList.get(0)), _entityClass);
    }


    public <T> List<T> findList() throws Exception {
        query();
        if(ToolsKit.isEmpty(resultList)) {
            return null;
        }
        if(ToolsKit.isEmpty(_entityClass)) {
            throw new EmptyNullException("entity class is null");
        }
        return (List<T>)ToolsKit.jsonParseArray(ToolsKit.toJsonString(resultList), _entityClass);
    }

    public <T> PageDto<T> findPage() throws Exception {
        PageDto pageDto = new PageDto();
        pageDto.setResult(findList());
        pageDto.setTotalCount(count());
        return pageDto;
    }
}
