package com.duangframework.mvc.core;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.MongoDao;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
public abstract class BaseService<T extends IdEntity> extends BaseCacheService<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    @Import
    private MongoDao<T> mongoDao;

    public MongoDao<T> getMongoDao() {
        return mongoDao;
    }

    private void setIdEntityData(T entity, String operatorId) {
        Date currentDate = new Date();
        String source = "";
        if(ToolsKit.isEmpty(entity.getId())) {
            entity.setId(new ObjectId().toString());
            entity.setCreatetime(currentDate);
            entity.setCreateuserid(operatorId);
            entity.setSource(source);
            entity.setStatus(IdEntity.STATUS_FIELD_SUCCESS);
            entity.setUpdatetime(currentDate);
            entity.setUpdateuserid(operatorId);
        } else {
            entity.setSource(source);
            entity.setUpdatetime(currentDate);
            entity.setUpdateuserid(operatorId);
        }
    }

    /**
     * 保存记录到数据库
     */
    protected  void add(T entity) {
        add(entity, Const.DUANG_SYSTEM_USERID, false);
    }

    /**
     * 保存记录到数据库及缓存
     */
    protected  void addDbAndCache(T entity, String operatorId) {
        add(entity, operatorId, true);
    }

    /**
     * 新增或修改后保存
     *
     * @param entity
     * @param operatorId 操作人ID
     * @return
     */
    protected boolean add(T entity, String operatorId, boolean isSaveCache) {
        entity.setId(null);
        setIdEntityData(entity, operatorId);
        try {
            if (getMongoDao().insert(entity)) {
                if (isSaveCache && getCacheDao() != null) {
                    getCacheDao().save(entity);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
    /**
     * 删除记录
     */
    protected void delete(String id) {

    }
    /**
     * 更改记录
     */
    protected void update() {

    }
    /**
     * 查找记录
     */
    protected void find() {

    }

}
