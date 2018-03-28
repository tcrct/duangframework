package com.duangframework.mongodb.convert.encode;


import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.convert.EncodeConvetor;

import java.lang.reflect.Field;

/**
 * Vo对象属性转换
 * @author laotang
 */
public class VoEncoder extends Encoder {

    public VoEncoder( Object value, Field field ) {
        super(value, field);
    }

    @Override
    public String getFieldName() {
        return ToolsKit.getFieldName(field);
    }

    @Override
    public Object getValue() {
        return EncodeConvetor.convetor(value);
    }
}
