package com.duangframework.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoDatabase;

/**
 *	自定义MongoDao对象
 *用于注入到Controller或Service等
 * @param <T>
 */
public class MongoDao<T>  extends MongoBaseDao<T> {

	public MongoDao(Class<T> cls) {
		super(cls);
	}

	public MongoDao(DB db, MongoDatabase database, Class<T> cls) {

		super(db, database, cls);
	}

	public DBCollection getDBCollection() {
		return super.coll;
	}
}
