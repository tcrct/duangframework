package com.duangframework.mvc.core;

import com.duangframework.cache.kit.CacheKit;
import com.duangframework.core.annotation.cache.EntityCache;
import com.duangframework.core.common.IdEntity;
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
public class BaseCacheService<T extends IdEntity>  {

    private static final Logger logger = LoggerFactory.getLogger(BaseCacheService.class);
    private static final BaseCacheService baseCacheService = new BaseCacheService();
    private static final Map<String, EntityCache> ENTITY_CACHE_MAP = new HashMap<>();
    private String keyPrefix = "";
    public BaseCacheService getCacheDao() {
        init();
        return baseCacheService;
    }

    private void init() {
        keyPrefix = PropertiesKit.duang().key("product.code").asString();
    }

    public boolean save(T entity) {
        String key = keyPrefix+":"+ClassUtils.getEntityName(entity.getClass(), true);
        int ttl = 600;
        String entityClassName = ClassUtils.getClassName(entity.getClass());
        EntityCache entityCache = ENTITY_CACHE_MAP.get(entityClassName);
        if(ToolsKit.isEmpty(entityCache)) {
            entityCache = entity.getClass().getAnnotation(EntityCache.class);
            ENTITY_CACHE_MAP.put(entityClassName, entityCache);
        } else {
            key = entityCache.key();
            ttl = entityCache.ttl();
        }
        try {
            Map<String, String> map = new HashMap<>();
            map.put(entity.getId(), ToolsKit.toJsonString(entity));
            return CacheKit.duang().hmset(key, map);
        } catch (Exception e) {
            logger.warn("save entity to cache is fail : " + e.getMessage(), e);
            return false;
        }
    }
}
