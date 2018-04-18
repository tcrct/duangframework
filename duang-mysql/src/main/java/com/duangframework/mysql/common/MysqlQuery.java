package com.duangframework.mysql.common;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mysql.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author Created by laotang
 * @date createed in 2018/3/26.
 */
public class MysqlQuery<T> {

    private final static Logger logger = LoggerFactory.getLogger(MysqlQuery.class);
    private Map<String, Object> queryObj;
    private Order order;
    private Field field;
    private CurdSqlModle curdSqlModle;
    public MysqlQuery() {
        curdSqlModle = new CurdSqlModle();
        queryObj = new HashMap();
        order = new Order();
        field = new Field();
    }

    /**
     * 等于
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> eq(String key, Object value){
        append2Mapping(key, Operator.EQ, value);
        return this;
    }

    /**
     * 不等于
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> ne(String key, Object value){
        append2Mapping(key, Operator.NE, value);
        return this;
    }

    /**
     * 大于(>)
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> gt(String key, Object value){
        append2Mapping(key, Operator.GT, value);
        return this;
    }

    /**
     *  大于等于(>=)
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> gte(String key, Object value){
        append2Mapping(key, Operator.GTE, value);
        return this;
    }

    /**
     * 小于(<)
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> lt(String key, Object value){
        append2Mapping(key, Operator.LT, value);
        return this;
    }

    /**
     * 小于等于(<=)
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> lte(String key, Object value){
        append2Mapping(key, Operator.LTE, value);
        return this;
    }

    /**
     * 模糊查询
     * @param key		字段名
     * @param value		内容值
     * @return
     */
    public MysqlQuery<T> like(String key, Object value) {
        append2Mapping(key, Operator.LIKE, value);
        return this;
    }

    /**
     * 多条件or查询
     * @param mysqlQueries	条件
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MysqlQuery<T> or(MysqlQuery... mysqlQueries) {
        List orMapList = (List)queryObj.get(Operator.OR);
        if(ToolsKit.isEmpty(orMapList)) {
            orMapList = new ArrayList();
            queryObj.put(Operator.OR, orMapList);
        }
        for( MysqlQuery q : mysqlQueries) {
            orMapList.add(q.getQueryObj());
        }
        return this;
    }

    /**
     * 多条件and查询
     * @param mysqlQueries	条件
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public MysqlQuery<T> and(MysqlQuery... mysqlQueries) {
        List andMapList = (List)queryObj.get(Operator.AND);
        if(ToolsKit.isEmpty(andMapList)) {
            andMapList = new ArrayList();
            queryObj.put(Operator.AND, andMapList);
        }
        for(MysqlQuery q : mysqlQueries) {
            andMapList.add(q.getQueryObj());
        }
        return this;
    }

    public MysqlQuery<T> sql(String sql) {
        return this;
    }

    public MysqlQuery<T> params(Object... params) {
        return this;
    }

    private void append2Mapping(String key, String oper, Object value){
        if(ToolsKit.isEmpty(key)) {
            throw new EmptyNullException("query key is null...");
        }
        Map<String, Object> map = null;
        Object obj = queryObj.get(key);
        if(obj instanceof Map){
            ((Map)obj).put(oper, value);
        } else {
            map = new HashMap<>();
            map.put(oper, value);
            queryObj.put(key, map);
        }
    }

    public Map<String, Object> getQueryObj() {
        TreeMap dbObject = new TreeMap(queryObj);
//        DBObject dbObject = new BasicDBObject(queryObj);
        System.out.println(dbObject.toString());
//        logger.info(" query: " + ToolsKit.toJsonString(queryObj));
        return queryObj;
    }

    public Map<String,String> getDBOrder() {
        logger.info(" order: " + ToolsKit.toJsonString(order.getDBOrder()));
        return order.getDBOrder();
    }

    public List<String> getDBField() {
        logger.info(" field: " + ToolsKit.toJsonString(field.getDBFields()));
        return field.getDBFields();
    }

    public MysqlQuery<T> fields(Field field) {
        this.field = field;
        return this;
    }

    public MysqlQuery<T> order(Order order) {
        this.order = order;
        return this;
    }

}

