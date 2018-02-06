package com.duangframework.ext.crud;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoQuery;
import com.duangframework.mongodb.common.MongoUpdate;
import com.duangframework.mongodb.utils.MongoUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
class CRUDService<T extends IdEntity> extends CRUDCacheService<T> {

    private static final Logger logger = LoggerFactory.getLogger(CRUDService.class);

    private MongoDao<T> mongoDao;

    private Class<T> entityClass;

    CRUDService(Class<T> entityClass) {
        this.entityClass =entityClass;
    }

    public MongoDao<T> getMongoDao() {
        if(ToolsKit.isEmpty(mongoDao)) {
            if(ToolsKit.isEmpty(entityClass)) {
                this.entityClass = ClassUtils.getSuperClassGenricType(getClass());
            }
            mongoDao = MongoUtils.getMongoDao(entityClass);
        }
        return mongoDao;
    }

    public Class<T> getEntityClass() {
        if(ToolsKit.isEmpty(entityClass)) {
            this.entityClass = ClassUtils.getSuperClassGenricType(getClass());
        }
        return entityClass;
    }

    private void addIdEntityData(T entity, boolean isSave) {
        if(!isSave && ToolsKit.isEmpty(entity.getId())) {
            entity.setId(new ObjectId().toString());
        }
        if(isSave){
            entity.setId(null);
        }
        String createUserId = entity.getCreateuserid();
        if(ToolsKit.isEmpty(createUserId)) {
            createUserId  = Const.DUANG_SYSTEM_USERID;
        }
        Date currentDate = new Date();
        String source = entity.getSource();
        if(ToolsKit.isEmpty(source)) {
            source = PropertiesKit.duang().key("default.source").defaultValue("phone").asString();
        }
        entity.setCreatetime(currentDate);
        entity.setCreateuserid(createUserId);
        entity.setSource(source);
        entity.setStatus(IdEntity.STATUS_FIELD_SUCCESS);
        entity.setUpdatetime(currentDate);
        entity.setUpdateuserid(createUserId);
    }

    private void updateIdEntityData(T entity) {
        if(ToolsKit.isEmpty(entity.getId())) {
            throw new EmptyNullException("entity id field is null");
        }
        if(ToolsKit.isEmpty(entity.getUpdateuserid())) {
            throw new EmptyNullException("entity updateuserid field is null");
        }
        Date currentDate = new Date();
        entity.setUpdatetime(currentDate);
    }

    /**
     * 保存新增记录到数据库及缓存
     *
     * @param entity
     * @return
     */
    public boolean add(T entity) throws ServiceException {
        try {
            addIdEntityData(entity, false);
            if (getMongoDao().insert(entity)) {
                if (getCacheDao() != null) {
                    getCacheDao().save(entity);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException("add entity is fail: " + e.getMessage(), e);
        }
    }
    /**
     * 删除记录
     */
    public boolean delete(String id) throws ServiceException {
        if(!ToolsKit.isValidDuangId(id)) {
            throw new ServiceException("it is not ObjectId");
        }
        MongoQuery<T> mongoQuery = new MongoQuery<>();
        mongoQuery.eq(IdEntity.ID_FIELD, id);
        MongoUpdate<T> mongoUpdate = new MongoUpdate<>();
        mongoUpdate.set(IdEntity.STATUS_FIELD, IdEntity.STATUS_FIELD_DELETE);
        try {
            long count = getMongoDao().update(mongoQuery, mongoUpdate);
            if(count > 0 && (getCacheDao() != null)) {
                getCacheDao().delete(entityClass, id);
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("delete id["+id+"] record is fail: " + e.getMessage(), e);
        }
  }
    /**
     * 更改记录
     */
    public boolean update(T entity) throws ServiceException {
        try {
            updateIdEntityData(entity);
            boolean isOk = getMongoDao().update(entity.getId(), (Document)MongoUtils.toBson(entity));
            if(isOk && (getCacheDao() != null)) {
                getCacheDao().save(entity);
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("update entity["+entity.getId()+"] record is fail: " + e.getMessage(), e);
        }

    }
    /**
     * 查找记录
     */
    @Override
    public <T> T findById(String id, Class<T> clazz) throws ServiceException{
        if(!ToolsKit.isValidDuangId(id)) {
            throw new ServiceException("it is not ObjectId");
        }
        T recordObj = null;
        try {
            if (getCacheDao() != null) {
                recordObj = (T)getCacheDao().findById(id, clazz);
                if(ToolsKit.isNotEmpty(recordObj)) {
                    return recordObj;
                }
            }
            MongoQuery<T> mongoQuery = new MongoQuery<>();
            mongoQuery.eq(IdEntity.ID_FIELD, id);
            recordObj = (T)getMongoDao().findOne(mongoQuery);
            if(ToolsKit.isNotEmpty(recordObj)) {
                if (getCacheDao() != null) {
                    getCacheDao().save((IdEntity) recordObj);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("delete id["+id+"] record is fail: " + e.getMessage(), e);
        }
        return recordObj;
    }

    /**
     * 保存记录
     */
    @Override
    public boolean save(T entity) throws ServiceException {
        try {
            String id = entity.getId();
            if(ToolsKit.isEmpty(id)) {
                addIdEntityData(entity, true);
            } else {
                updateIdEntityData(entity);
            }
            if(getMongoDao().save(entity)) {
                if(getCacheDao() != null) {
                    getCacheDao().save(entity);
                }
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("save entity["+entity.getId()+"] record is fail: " + e.getMessage(), e);
        }
    }

}
