package com.duangframework.mongodb.convert.encode;

import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.convert.EncodeConvetor;
import org.bson.Document;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 *
 * @author laotang
 */
public class VoCollEncoder extends Encoder {

    public VoCollEncoder( Object value, Field field ) {
        super(value, field);
    }

    @Override
    public String getFieldName() {
        return ToolsKit.getFieldName(field);
    }

    @Override
    public Object getValue() {
        Object result = null;
        Class<?> fieldType = field.getType();
        if(fieldType.isArray()){
            result = encodeArray();
        } else {
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] paramTypes = paramType.getActualTypeArguments();
            if(paramTypes.length == 1) {
                result = encodeCollection();
            } else if (paramTypes.length == 2){
                result = encodeMap();
            }
        }
        return result;
    }

    private Object encodeArray(){
        int length = Array.getLength(value);
        List<Document> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Object obj = Array.get(value, i);
            if(ToolsKit.isNotEmpty(obj)){
                result.add(EncodeConvetor.convetor(obj));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object encodeCollection(){
        List<Document> result = new ArrayList<>();
        Collection coll = (Collection)value;
        if(null != coll){
            for(Iterator it = coll.iterator(); it.hasNext();){
                Object obj = it.next();
                if(ToolsKit.isNotEmpty(obj)){
                    result.add(EncodeConvetor.convetor(obj));
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object encodeMap(){
        Map map = (Map)value;
        Map result = new HashMap(map.size());
        for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
            String key = it.next();
            Object obj = map.get(key);
            if(ToolsKit.isNotEmpty(obj)){
                result.put(key, EncodeConvetor.convetor(obj));
            } else {
                result.put(key, null);
            }
        }
        return result;
    }
}
