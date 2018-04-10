package com.duangframework.mongodb.utils;

import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.MongodbException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.convert.EncodeConvetor;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Created by laotang
 * @date on 2017/11/21.
 */
public class MongoUtils {

    private static ConcurrentMap<String, MongoDao<?>> MONGODAO_MAP = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(MongoUtils.class);


    public static Object toObjectIds(Object values) {
        if(values instanceof Object[]){
            List<ObjectId> idList = new ArrayList<ObjectId>();
            Object[] tmp = (Object[]) values;
            for (Object value : tmp) {
                if (value != null) {
                    boolean isObjectId = ToolsKit.isValidDuangId(value.toString());
                    if (isObjectId) {
                        ObjectId dbId = new ObjectId(value.toString());
                        idList.add(dbId);
                    }
                }
            }
            return idList;
        } else {
            boolean isObjectId = ToolsKit.isValidDuangId(values.toString());
            if (isObjectId) {
                return new ObjectId(values.toString());
            } else {
                throw new IllegalArgumentException("toObjectIds is fail");
            }
        }
    }

    public static ObjectId toObjectId(String objId) {
        if (ToolsKit.isEmpty(objId) || !ObjectId.isValid(objId)) {
            throw new MongodbException("toObjectId is Fail: ["+objId+"] is not ObjectId or Empty");
        }
        return new ObjectId(objId);
    }

    /**
     *  将取出的类属性字段转换为Mongodb的DBObject
     * @param fields
     * @return
     */
    public static DBObject convert2DBFields(Field[] fields) {
        if (ToolsKit.isEmpty(fields)) {
            return null;
        }
        DBObject dbo = new BasicDBObject();
        for (int i = 0; i < fields.length; i++) {
            dbo.put(fields[i].getName(), true);
        }
        return dbo;
    }

    public static DBObject convert2DBFields(Collection<String> coll) {
        if (ToolsKit.isEmpty(coll)) {
            return null;
        }
        DBObject fieldsObj = new BasicDBObject();
        for (Iterator<String> it = coll.iterator(); it.hasNext();) {
            fieldsObj.put(it.next(), true);
        }
        return fieldsObj;
    }

    public static <T> T toBson(Object obj) {
        if(null == obj) {
            throw new EmptyNullException("toBson is fail:  obj is null");
        }
        try {
            return (T)EncodeConvetor.convetor(obj);
        } catch (Exception e) {
            throw new MongodbException("toBson is fail: " + e.getMessage(), e);
        }
    }

//    public static DBObject toDBObject(Object obj) {
//        if (null == obj) {
//            return null;
//        }
//        DBObject dbo = new BasicDBObject();
//        Field[] fields = ClassUtils.getFields(obj.getClass());
//        for (Field field : fields) {
//            Encoder encoder = EncoderFactory.create(obj, field);
//            if (encoder != null && !encoder.isNull()) {
//                dbo.put(encoder.getFieldName(), encoder.getValue());
//            }
//        }
//        return dbo;
//    }

    public static <T> T toEntity(Document document, Class<?> clazz) {
        try {
            document = convert2EntityId(document);
            return (T) ToolsKit.jsonParseObject(document.toJson(), clazz);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将_id改为id字段
     * @param document
     * @return
     */
    private static Document convert2EntityId(Document document) {
        try {
            if (document == null || document.get(IdEntity.ID_FIELD) == null) {
                return document;
            } else {
                document.put(IdEntity.ENTITY_ID_FIELD, document.get(IdEntity.ID_FIELD).toString());
            }
        } catch (ClassCastException e) {
                /*如果转换出错直接返回原本的值,不做任何处理*/
        }
        return document;
    }

    /**
     * 将id字段更改为_id
     * @param document
     * @return
     */
    public static Document convert2ObjectId(Document document) {
        if(ToolsKit.isEmpty(document)) {
            throw  new MongodbException("convert2ObjectId is fail: document is null");
        }
        String id = document.getString(IdEntity.ENTITY_ID_FIELD);
        if (ToolsKit.isEmpty(id)) {
            id = document.getString(IdEntity.ID_FIELD);
        }
        if (ToolsKit.isNotEmpty(id)) {
            document.put(IdEntity.ID_FIELD, MongoUtils.toObjectId(id));
        }
        return document;
    }

    /**
     * 根据Entity类取出MongoDao
     * @param cls           继承了IdEntity的类
     * @param <T>
     * @return
     */
    public static <T> MongoDao<T> getMongoDao(Class<T> cls){
        String key = ClassUtils.getEntityName(cls);
        MongoDao<?> dao = MONGODAO_MAP.get(key);
        if(null == dao){
            dao = new MongoDao<T>(cls);
            MONGODAO_MAP.put(key, dao);
        }
        return (MongoDao<T>)dao;
    }

    /**
     * 构建排序对象
     * @param fieldName 要排序的字段
     * @param orderBy	排序字符串，asc(1)或desc(-1)
     * @return
     */
    public static DBObject builderOrder(String fieldName, String orderBy){
        if(ToolsKit.isEmpty(fieldName) || ToolsKit.isEmpty(orderBy)) {
            return null;
        }
        if(ToolsKit.isEmpty(orderBy)){
            return BasicDBObjectBuilder.start(IdEntity.ID_FIELD, -1).get();		//默认用OID时间倒序
        }else{
            return BasicDBObjectBuilder.start(fieldName, "desc".equalsIgnoreCase(orderBy) ? -1 : 1).get();
        }
    }
}
