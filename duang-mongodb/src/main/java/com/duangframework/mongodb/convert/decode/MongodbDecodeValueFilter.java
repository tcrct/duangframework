package com.duangframework.mongodb.convert.decode;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.DataType;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * @author Created by laotang
 * @date createed in 2018/4/20.
 */
public class MongodbDecodeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
//        System.out.println(object+"           "+name+"           "+value+"      "+ value.getClass());
        if (IdEntity.ID_FIELD.equals(name)) {
            return ((ObjectId)value).toString();
        }
        if(DataType.isDate(value.getClass()) || DataType.isTimestamp(value.getClass())) {
            Date date = (Date)value;
            return ToolsKit.formatDate(date, Const.DEFAULT_DATE_FORM);
        }
        return value;
    }
}
