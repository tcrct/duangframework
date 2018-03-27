package com.duangframework.mysql.convert;

import com.duangframework.core.annotation.db.Vo;
import com.duangframework.core.annotation.db.VoColl;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mysql.common.CurdEnum;
import com.duangframework.mysql.common.CurdSqlModle;
import com.duangframework.mysql.convert.encode.Encoder;
import com.duangframework.mysql.convert.encode.PropertyEncoder;
import com.duangframework.mysql.convert.encode.VoEncoder;
import com.duangframework.mysql.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Created by laotang
 * @date createed in 2018/3/26.
 */
public class EncodeConvetor {

    private final static Logger logger = LoggerFactory.getLogger(EncodeConvetor.class);

    public static CurdSqlModle encode(Object entity, CurdEnum curdEnum) {
        Class<?> entityClass = entity.getClass();
        boolean isExtends = ClassUtils.isExtends(entityClass, IdEntity.class.getCanonicalName());
        if(!isExtends){
            throw new RuntimeException("the "+entityClass.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
        }
        CurdSqlModle curdSqlModle = null;
        try {
            String entityJson = ToolsKit.toJsonString(entity);
            if (ToolsKit.isNotEmpty(entityJson)) {
                Map<String, Object> map = ToolsKit.jsonParseObject(entityJson, Map.class);
                String idFieldName = MysqlUtils.getIdFieldName(entityClass);
                curdSqlModle = MysqlUtils.builderSqlModle(curdEnum, entityClass, map, idFieldName);
            }
        } catch (Exception e) {
            throw new MysqlException(e.getMessage(), e);
        }
        return curdSqlModle;
    }


    public static Encoder parser(Object obj, Field field) {
        Encoder encoder = null;
        if(null != field.getAnnotation(Vo.class)){
            encoder = new VoEncoder(obj, field);
        } else if (null != field.getAnnotation(VoColl.class)){
//            encoder = new VoCollEncoder(obj, field);
        } else {
            encoder = new PropertyEncoder(obj, field);
        }
        return encoder;
    }
}
