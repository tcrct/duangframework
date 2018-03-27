package com.duangframework.mysql;

import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mysql.common.CurdEnum;
import com.duangframework.mysql.common.CurdSqlModle;
import com.duangframework.mysql.common.MysqlQuery;
import com.duangframework.mysql.common.MysqlUpdate;
import com.duangframework.mysql.convert.EncodeConvetor;
import com.duangframework.mysql.kit.MysqlKit;
import com.duangframework.mysql.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/3/26.
 */
public class MysqlDao <T> implements IDao<T> {

    private final static Logger logger = LoggerFactory.getLogger(MysqlDao.class);
    protected Class<T> entityClass;

    public MysqlDao(final Class<T> cls){
        init(cls);
    }

    private void init(final Class<T> cls){
        boolean isExtends = ClassUtils.isExtends(cls, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        this.entityClass = cls;
        String databaseName = MysqlUtils.getDataBaseName(entityClass);
        String tableName = ClassUtils.getEntityName(entityClass);
        try {
            MysqlUtils.createTables(databaseName, tableName, entityClass);
            MysqlUtils.createIndexs(databaseName, tableName, entityClass);
        } catch (Exception e) {
            logger.warn("init "+cls.getName()+" table fail: " + e.getMessage(), e);
        }
//		new SqlListener().onEvent(new SqlEvent(cls));
    }

    @Override
    public boolean save(T entity) throws Exception {
        CurdSqlModle curdSqlModle = null;
        IdEntity idEntity = (IdEntity)entity;
        boolean isInsert = ToolsKit.isEmpty(idEntity.getId());
        try {
            curdSqlModle = EncodeConvetor.encode(entity, isInsert ? CurdEnum.INSERT : CurdEnum.UPDATE);
        } catch (Exception e) {
            throw  new MysqlException("build CurdSqlModle is fail: " + e.getMessage(), e);
        }
        MysqlKit mysqlKit = MysqlKit.duang().entityClass(entityClass).params(curdSqlModle.getParamValueArray());
        if(isInsert) {
            return mysqlKit.sql(curdSqlModle.builderInsertSql()).add();
        } else {
            return mysqlKit.sql(curdSqlModle.builderUpdateSql()).update();
        }
    }

    @Override
    public long update(MysqlQuery mysqlQuery, MysqlUpdate mysqlUpdate) throws Exception {
        return 0;
    }

    @Override
    public <T1> T1 findOne(MysqlQuery mysqlQuery) throws Exception {
        return null;
    }

    @Override
    public List<T> findList(MysqlQuery mysqlQuery) throws Exception {
        return null;
    }
}
