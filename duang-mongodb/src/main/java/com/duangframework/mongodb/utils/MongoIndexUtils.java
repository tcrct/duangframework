package com.duangframework.mongodb.utils;

import com.duangframework.core.annotation.db.Index;
import com.duangframework.core.annotation.db.Vo;
import com.duangframework.core.annotation.db.VoColl;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 索引创建工具类
 * @author Created by laotang
 * @date on 2017/11/22.
 */
public class MongoIndexUtils {

    private static Logger logger = LoggerFactory.getLogger(MongoIndexUtils.class);

    private static ConcurrentMap<String, Index> INDEX_MAP = new ConcurrentHashMap<>();

    /**
     * 创建索引
     *
     * @param coll
     *            collection
     * @param cls
     *            entity class
     */
    public static void createIndex(DBCollection coll, Class<?> cls) throws Exception {
        Field[] fields = ClassUtils.getFields(cls);
        if (ToolsKit.isEmpty(fields)) {
            return;
        }
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            boolean isVoField = fields[i].isAnnotationPresent(Vo.class);
            boolean isVoCollField = fields[i].isAnnotationPresent(VoColl.class);
            if (isVoField || isVoCollField) {
                Class<?> clazz = null;
                if (isVoField) {
                    clazz = fields[i].getType();
                } else if (isVoCollField) {
                    clazz = getVoCollFieldClass(fields[i]);
                }
                if (ToolsKit.isEmpty(clazz)) {
                    continue;
                }
                createVoIndexKey(clazz, key + ".");
                if (INDEX_MAP.size() > 0) {
                    for (Iterator<Map.Entry<String, Index>> it = INDEX_MAP.entrySet().iterator(); it.hasNext();) {
                        Map.Entry<String, Index> entry = it.next();
                        createIndex(coll, entry.getKey(), entry.getValue());
                    }
                }
                INDEX_MAP.clear();
            }
            Index index = fields[i].getAnnotation(Index.class);
            if (ToolsKit.isNotEmpty(index)) {
                createIndex(coll, key, index);
            }
        }
    }

    private static void createIndex(DBCollection coll, String key, Index index) throws Exception {
        String name = ToolsKit.isEmpty(index.name()) ? "_" + key + "_" : index.name();
        if (name.length() < 3 && name.length()>120) return;
        String type = "text".equalsIgnoreCase(index.type()) ? "1" : "2d";
        String order = index.order().toLowerCase();
        DBObject keys = new BasicDBObject(key, "asc".equals(order) ? Integer.parseInt("1") : Integer.parseInt("-1"));
        if ("2d".equals(type)) {
            keys.put(key, type);
        }
        DBObject options = new BasicDBObject("background", true);
        options.put("name", name.toLowerCase()); // 将index的name全部统一设置为小写
        options.put("unique", index.unique());
		 logger.debug("#########MongoKit CreateIndex Key: "+keys.toString()+"["+key.length()+"] Options: " + options.toString()+" Coll Name: " + coll.getFullName());
        coll.createIndex(keys, options);
    }

    private static void createVoIndexKey(Class<?> cls, String key) throws Exception {
        Field[] fields = ClassUtils.getFields(cls);
        if (ToolsKit.isNotEmpty(fields)) {
            for (Field field : fields) {
                boolean isVoField = field.isAnnotationPresent(Vo.class);
                boolean isVoCollField = field.isAnnotationPresent(VoColl.class);
                if (isVoField || isVoCollField) {
                    Class<?> clazz = null;
                    if (isVoField) {
                        clazz = field.getType();
                    } else if (isVoCollField) {
                        clazz = getVoCollFieldClass(field);
                    }
                    if (ToolsKit.isEmpty(clazz)) {  continue; }
                    String key2 = key + field.getName() + ".";
                    createVoIndexKey(clazz, key2);
                } else {
                    Index index = field.getAnnotation(Index.class);
                    if (ToolsKit.isNotEmpty(index)) {
                        if (ToolsKit.isNotEmpty(key) && (key.contains("."))) {
                            String indexKey = key.substring(0, key.lastIndexOf(".") + 1) + field.getName();
                            INDEX_MAP.put(indexKey, index);
                        }
                    }
                }
            }
        }
    }

    private static Class<?> getVoCollFieldClass(Field field) {
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
        Type[] paramTypes = paramType.getActualTypeArguments();
        if (paramTypes.length == 1) {
            return getParamTypesItemClass(paramTypes[0]);
        } else if (paramTypes.length == 2) {
            return getParamTypesItemClass(paramTypes[1]);
        }
        return null;
    }

    @SuppressWarnings("restriction")
    private static Class<?> getParamTypesItemClass(Type type) {
        if (type.getClass().equals(sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl.class)) {
            ParameterizedType paramTypeItem = (ParameterizedType) type;
            Type[] paramTypesItem = paramTypeItem.getActualTypeArguments();
            if (paramTypesItem.length == 1) {
                return getParamTypesItemClass(paramTypesItem[0]);
            } else if (paramTypesItem.length == 2) {
                return getParamTypesItemClass(paramTypesItem[1]);
            }
        }
        return (Class<?>) type;
    }

}
