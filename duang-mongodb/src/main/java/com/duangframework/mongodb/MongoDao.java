package com.duangframework.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDao<T>  extends MongoBaseDao<T> {

	public MongoDao() {

	}

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
