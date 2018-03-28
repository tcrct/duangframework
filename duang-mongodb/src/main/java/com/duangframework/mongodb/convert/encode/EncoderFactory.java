package com.duangframework.mongodb.convert.encode;


import com.duangframework.core.annotation.db.Id;
import com.duangframework.core.annotation.db.Vo;
import com.duangframework.core.annotation.db.VoColl;

import java.lang.reflect.Field;

public final class EncoderFactory {
    
    public static Encoder create(Object obj, Field field){
		Encoder encoder = null;
		if (null != field.getAnnotation(Id.class)) {
//			encoder = new IdEncoder(obj, field);
		} else if ((null != field.getAnnotation(Vo.class))) {
//			encoder = new VoEncoder(obj, field);
		} else if ((null != field.getAnnotation(VoColl.class)) ) {
//			encoder = new VoCollEncoder(obj, field);
		} else {
//			encoder = new PropertyEncoder(obj, field);
		}
		return encoder;
	}
}
