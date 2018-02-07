package com.duangframework.ext.curd;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoQuery;
import com.duangframework.mongodb.common.MongoUpdate;
import com.duangframework.mongodb.utils.MongoUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
class CurdService<T extends IdEntity> extends CurdCacheService<T> {

    private static final Logger logger = LoggerFactory.getLogger(CurdService.class);

    private MongoDao<T> mongoDao;

    private Class<T> entityClass;

    CurdService(Class<T> entityClass) {
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

    private void addIdEntityData(T entity) {
        if(ToolsKit.isEmpty(entity.getId())) {
            entity.setId(new ObjectId().toString());
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
        entity.setUpdatetime(new Date());
    }

    /**
     * 保存新增记录到数据库及缓存
     *
     * @param entity
     * @return
     */
    public boolean add(T entity) throws ServiceException {
        try {
            addIdEntityData(entity);
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
            boolean isOk = getMongoDao().update(entity.getId(), entity);
            if(isOk && (getCacheDao() != null)) {
                T cacheEntity = (T)getCacheDao().findById(entity.getId(), getEntityClass());
                ObjectKit.copyFields(entity, cacheEntity);
                if(ToolsKit.isNotEmpty(entity.getCreateuserid())) {
                    cacheEntity.setCreateuserid(entity.getCreateuserid());
                }
                if(ToolsKit.isNotEmpty(entity.getCreatetime())) {
                    cacheEntity.setCreatetime(entity.getCreatetime());
                }
                if(ToolsKit.isNotEmpty(entity.getId())) {
                    cacheEntity.setId(entity.getId());
                }
                if(ToolsKit.isNotEmpty(entity.getSource())) {
                    cacheEntity.setSource(entity.getSource());
                }
                if(ToolsKit.isNotEmpty(entity.getStatus())) {
                    cacheEntity.setStatus(entity.getStatus());
                }
                if(ToolsKit.isNotEmpty(entity.getUpdatetime())) {
                    cacheEntity.setUpdatetime(entity.getUpdatetime());
                }
                if(ToolsKit.isNotEmpty(entity.getUpdateuserid())) {
                    cacheEntity.setUpdateuserid(entity.getUpdateuserid());
                }
                getCacheDao().save(cacheEntity);
            }
            return true;
        } catch (Exception e) {
            throw new ServiceException("update entity["+entity.getId()+"] record is fail: " + e.getMessage(), e);
        }

    }
    /**
     * 查找记录
     */
    public <T> T findById(String id) throws ServiceException{
        if(!ToolsKit.isValidDuangId(id)) {
            throw new ServiceException("it is not ObjectId");
        }
        T recordObj = null;
        try {
            if (getCacheDao() != null) {
                recordObj = (T)getCacheDao().findById(id, getEntityClass());
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
            throw new ServiceException("findById id["+id+"] record is fail: " + e.getMessage(), e);
        }
        return recordObj;
    }

    /**
     * 保存记录
     */
    @Override
    public boolean save(T entity) throws ServiceException {
        boolean isOk;
        try {
            String id = entity.getId();
            if(ToolsKit.isEmpty(id)) {
                isOk = add(entity);
            } else {
                isOk= update(entity);
            }
        } catch (Exception e) {
            throw new ServiceException("save entity["+entity.getId()+"] record is fail: " + e.getMessage(), e);
        }
        return isOk;
    }

}
