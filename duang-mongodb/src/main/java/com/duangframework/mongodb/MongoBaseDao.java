package com.duangframework.mongodb;

import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.common.MongoQuery;
import com.duangframework.mongodb.common.MongoUpdate;
import com.duangframework.mongodb.kit.MongoClientKit;
import com.duangframework.mongodb.utils.MongoIndexUtils;
import com.duangframework.mongodb.utils.MongoUtils;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class MongoBaseDao<T> implements IDao<T> {

	private final static Logger logger = LoggerFactory.getLogger(MongoBaseDao.class);
	
	protected Class<T> cls;
	protected DB mongoDB;
	protected DBCollection coll;
	protected MongoDatabase mongoDatabase;
	protected MongoCollection<Document> collection;
	protected DBObject keys;
	
	public MongoBaseDao(){
	}
	
	public MongoBaseDao(Class<T> cls){
		init(MongoClientKit.duang().getDefaultDB(),
				MongoClientKit.duang().getDefaultMongoDatabase(),
				cls);
	}

	public MongoBaseDao(DB db, MongoDatabase database, Class<T> cls) {
		init(db, database, cls);
	}

	private void init(DB db, MongoDatabase database, Class<T> cls){
		boolean isExtends = ClassUtils.isExtends(cls, IdEntity.class.getCanonicalName());
		if(!isExtends){
			throw new RuntimeException("the "+cls.getCanonicalName()+" is not extends "+ IdEntity.class.getCanonicalName() +", exit...");
		}
		this.cls = cls;
		try{
			mongoDB = db;
			mongoDatabase = database;
			// 根据类名或指定的name创建表名
			String entityName = ClassUtils.getEntityName(cls);
	 		coll = mongoDB.getCollection(entityName);
			collection = mongoDatabase.getCollection(entityName);
			keys = MongoUtils.convert2DBFields(ClassUtils.getFields(cls));
			MongoIndexUtils.createIndex(coll, cls);
		} catch(Exception e){
			e.printStackTrace();
			logger.error(coll.getFullName()+" Create Index Fail: " + e.getMessage());
		}
	}

	/**
	 * 持久化到数据库, 会自己根据entity是否有id值进行保存或更新操作
	 * @param entity	需要持久化的对象
	 * @return			正确持久化到数据库返回true, 否则执行出异常
	 */
	@Override
	public boolean save(T entity) throws Exception {
		IdEntity idEntity = (IdEntity)entity;
		if(ToolsKit.isEmpty(idEntity.getId())){
			idEntity.setId(null);
		}
		return doSaveOrUpdate(idEntity);
	}
	
	private boolean doSaveOrUpdate(IdEntity entity) throws Exception {
		Document document = MongoUtils.toDocument(entity);
		String id = entity.getId();
		try {
			if(ToolsKit.isEmpty(id)) {
				collection.insertOne(document);
			} else {
				Document filterDoc = new Document();
				filterDoc.put(IdEntity.ID_FIELD, new ObjectId(id));
				document.remove(IdEntity.ENTITY_ID_FIELD);
//				replaceOne该方法仅支持mongodb3.0以上的版本
				collection.replaceOne(filterDoc, document);
			}
			return true;
		}catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
	}


	@Override
	public long update(MongoQuery mongoQuery, MongoUpdate mongoUpdate) throws Exception {
		Document queryDoc = mongoQuery.getQueryDoc();
		Document updateDoc = mongoUpdate.getUpdateDoc();
		if(ToolsKit.isEmpty(queryDoc) || ToolsKit.isEmpty(updateDoc)) {
			throw new EmptyNullException("Mongodb Update is Fail: queryDoc or updateDoc is null");
		}
//		BsonDocument bsonDocument = document.toBsonDocument(cls, collection.getCodecRegistry());
		UpdateResult updateResult = collection.updateOne(queryDoc, updateDoc);
		return updateResult.isModifiedCountAvailable() ? updateResult.getModifiedCount() : 0L;
	}

	/**
	 *
	 * @param mongoQuery
	 * @return
	 * @throws Exception
	 */
	@Override
	public T findOne(MongoQuery mongoQuery) throws Exception {
		Document queryDoc = mongoQuery.getQueryDoc();
		if(ToolsKit.isEmpty(queryDoc)) {
			throw new EmptyNullException("Mongodb findOne is Fail: queryDoc or updateDoc is null");
		}
		Document document = collection.find(queryDoc).first();
		if(ToolsKit.isEmpty(document)) {
			return null;
		}
		return MongoUtils.toEntity(document, cls);
	}

	/**
	 *
	 * @param mongoQuery
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<T> findList(MongoQuery mongoQuery) throws Exception {
		Document queryDoc = mongoQuery.getQueryDoc();
		if(ToolsKit.isEmpty(queryDoc)) {
			throw new EmptyNullException("Mongodb findList is Fail: queryDoc is null");
		}

		FindIterable<Document> documents = collection.find(queryDoc);
		if(ToolsKit.isEmpty(documents)) {
			return null;
		}
		final List<T> resultList = new ArrayList();
		documents.forEach(new Block<Document>() {
			@Override
			public void apply(Document document) {
				resultList.add((T)MongoUtils.toEntity(document, cls));
			}
		});
		return resultList;
	}

}
