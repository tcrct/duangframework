package com.duangframework.mysql;


import com.duangframework.mysql.common.MysqlQuery;
import com.duangframework.mysql.common.MysqlUpdate;

import java.util.List;

/**
 * Dao接口，定义所有方法接口
 * @author laotang
 * @param <T>
 */
public interface IDao<T> {
	
	/**
	 * 保存对象
	 * @param 		待保存的对象
	 * @return		对象
	 */
	boolean save(T entity) throws Exception;
	
	
	/**
	 * 根据条件更新字段
	 * @param mysqlQuery			查询条件
	 * @param mysqlUpdate		更新内容
	 * @return
	 * @throws Exception
	 */
	long update(MysqlQuery mysqlQuery, MysqlUpdate mysqlUpdate) throws Exception;
	
	/**
	 * 根据Query查找对象
	 * @param key		属性字段
	 * @param value		值
	 * @return			对象
	 */
	<T> T findOne(MysqlQuery mysqlQuery) throws Exception;
	
	/**
	 * 根据Query查找对象集合
	 * @param mongoQuery
	 * @return
	 * @throws Exception
	 */
	List<T> findList(MysqlQuery mysqlQuery) throws Exception ;

}
