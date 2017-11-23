package com.duangframework.mongodb.common;

/**
 * 执行MongoDB操作
 * @author laotang
 *
 */
public class ExceOper {

	private String databaseName;
	private String collectionName;
	private MongoQuery mongoQuery;
	private Field field; 
	private Order order;
	private MongoUpdate mongoUpdate;
	public ExceOper() {
		super();
	}
	public ExceOper(String databaseName, String collectionName, MongoQuery mongoQuery, MongoUpdate mongoUpdate, Field field, Order order) {
		super();
		this.databaseName = databaseName;
		this.collectionName = collectionName;
		this.mongoQuery = mongoQuery;
		this.mongoUpdate = mongoUpdate;
		this.field = field;
		this.order = order;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	public MongoQuery getMongoQuery() {
		return mongoQuery;
	}
	public void setMongoQuery(MongoQuery mongoQuery) {
		this.mongoQuery = mongoQuery;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public MongoUpdate getMongoUpdate() {
		return mongoUpdate;
	}
	public void setMongoUpdate(MongoUpdate mongoUpdate) {
		this.mongoUpdate = mongoUpdate;
	}
	@Override
	public String toString() {
		return "ExceOper [databaseName=" + databaseName + ", collectionName=" + collectionName + ", mongoQuery=" + mongoQuery
				+ ", field=" + field + ", order=" + order + ", mongoUpdate=" + mongoUpdate + "]";
	}
}
