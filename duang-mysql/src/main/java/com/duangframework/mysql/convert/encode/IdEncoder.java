package com.duangframework.mysql.convert.encode;


import com.duangframework.core.annotation.db.Id;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.UUID;

public class IdEncoder extends Encoder {

	private final static Logger logger = LoggerFactory.getLogger(IdEncoder.class);

	private Id id;

	public IdEncoder(Object obj, Field field) {
		super(obj, field);
		id = field.getAnnotation(Id.class);
	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public String getFieldName() {
		return IdEntity.ID_FIELD;
	}

	@Override
	public Object getValue() {
		Object result = null;
		try {
			result = fixIdValue();
		} catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
		}
		return result;
	}

	private Object fixIdValue() throws ServiceException {
		Object result = null;
		switch (id.type()) {
		case OID:
			if (value == null) {
				result = new DuangId();
			} else {
				result = new DuangId(value.toString());
			}
			break;
		case UUID:
			if (value == null) {
				result = UUID.randomUUID().toString().replaceAll("-","");
			} else {
				result = value.toString();
			}
			break;
		case CUSTOM:
			if (value == null) {
				throw new NullPointerException("user-defined id doesn't have value!");
			} else {
				result = value.toString();
			}
			break;
		default:
			result = null;
		}
		return result;
	}
}
