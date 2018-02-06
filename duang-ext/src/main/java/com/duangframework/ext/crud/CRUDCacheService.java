package com.duangframework.ext.crud;

import com.duangframework.cache.kit.CacheKit;
import com.duangframework.core.annotation.cache.EntityCache;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.ServiceException;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/1/31.
 */
public class CRUDCacheService<T extends IdEntity>  {

    private static final Logger logger = LoggerFactory.getLogger(CRUDCacheService.class);
    private static final CRUDCacheService CRUD_CACHE_SERVICE = new CRUDCacheService();
    private static final Map<String, EntityCache> ENTITY_CACHE_MAP = new HashMap<>();
    private static String keyPrefix = "";
    public CRUDCacheService getCacheDao() {
        init();
        return CRUD_CACHE_SERVICE;
    }

    private void init() {
        if(ToolsKit.isEmpty(keyPrefix)) {
            keyPrefix = PropertiesKit.duang().key("product.code").asString();
        }
    }

    /**
     * 确定缓存相关值
     * @param clazz
     * @return
     */
    private EntityCacheModle getEntityCacheModle(Class clazz) {
        String key = keyPrefix+":"+ClassUtils.getEntityName(clazz, true);
        int ttl = 600;
        String entityClassName = ClassUtils.getClassName(clazz);
        EntityCache entityCache = ENTITY_CACHE_MAP.get(entityClassName);
        if(ToolsKit.isEmpty(entityCache)) {
            if(clazz.isAnnotationPresent(EntityCache.class)) {
                ENTITY_CACHE_MAP.put(entityClassName, (EntityCache) clazz.getAnnotation(EntityCache.class));
            }
        } else {
            key = entityCache.key();
            ttl = entityCache.ttl();
        }
        return new EntityCacheModle(key, ttl);
    }

    /**
     * 保存对象到缓存中
     * @param entity
     * @return
     * @throws ServiceException
     */
    public boolean save(T entity) throws ServiceException {
        EntityCacheModle modle = getEntityCacheModle(entity.getClass());
        try {
            // 保存到缓存中
            long count = CacheKit.duang().hset(modle.getKey(), entity.getId(), ToolsKit.toJsonString(entity));
            if(count > 0 ) {
                //设置过期时间,
                CacheKit.duang().expire(modle.getKey(), modle.getTtl());
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.warn("save entity to cache is fail : " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据ID查找缓存里的值
     * @param id
     * @param clazz
     * @param <T>
     * @return
     * @throws ServiceException
     */
    public <T> T findById(String id, Class<T> clazz) throws ServiceException {
        EntityCacheModle modle = getEntityCacheModle(clazz);
        String jsonString = CacheKit.duang().hget(modle.getKey(), id);
        if(ToolsKit.isNotEmpty(jsonString)) {
            return ToolsKit.jsonParseObject(jsonString, clazz);
        }
        return null;
    }

    /**
     * 根据ID删除缓存值
     * @param clazz
     * @param id
     * @return
     * @throws ServiceException
     */
    public boolean delete(Class clazz, String id) throws ServiceException {
        EntityCacheModle modle = getEntityCacheModle(clazz);
        long count = CacheKit.duang().hdel(modle.getKey(), id);
        return count > 0;
    }
}
