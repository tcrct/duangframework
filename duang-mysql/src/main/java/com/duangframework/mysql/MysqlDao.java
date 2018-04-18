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
    protected String myClientCode = "";

    public MysqlDao(final String clientCode, final Class<T> cls){
        init(clientCode, cls);
    }

    private void init(final String clientCode, final Class<T> cls){
        boolean isExtends = ClassUtils.isExtends(cls, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        this.entityClass = cls;
        this.myClientCode = clientCode;
        String tableName = ClassUtils.getEntityName(entityClass);
        try {
//            MysqlUtils.createTables(databaseName, tableName, entityClass);
//            MysqlUtils.createIndexs(databaseName, tableName, entityClass);
        } catch (Exception e) {
            logger.warn("init "+cls.getName()+" table fail: " + e.getMessage(), e);
        }
//		new SqlListener().onEvent(new SqlEvent(cls));
    }

    @Override
    public boolean save(T entity) throws Exception {
        CurdSqlModle curdSqlModle = null;
        IdEntity idEntity = (IdEntity)entity;
        boolean isInsert = ToolsKit.isEmpty(idEntity.getMysqlId()) || ToolsKit.isEmpty(idEntity.getId());
        try {
            curdSqlModle = EncodeConvetor.convetor(entity, isInsert ? CurdEnum.INSERT : CurdEnum.UPDATE);
        } catch (Exception e) {
            throw  new MysqlException("build CurdSqlModle is fail: " + e.getMessage(), e);
        }
        MysqlKit mysqlKit = MysqlKit.duang()
                .use(myClientCode)
                .entityClass(entityClass)
                .params(curdSqlModle.getParamValueArray());
        if(isInsert) {
            int idNum =  mysqlKit.sql(curdSqlModle.builderInsertSql()).add();
            idEntity.setId(idNum);
            return idNum > 0 ? true : false;
        } else {
            return mysqlKit.sql(curdSqlModle.builderUpdateSql()).update();
        }
    }

    @Override
    public long update(MysqlQuery mysqlQuery, MysqlUpdate mysqlUpdate) throws Exception {
        return 0;
    }

    @Override
    public <T> T findOne(MysqlQuery mysqlQuery) throws Exception {
        return null;
    }

    @Override
    public List<T> findList(MysqlQuery mysqlQuery) throws Exception {
        return null;
    }
}
