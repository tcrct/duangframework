package com.duangframework.mysql.core;

import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mysql.kit.MysqlKit;
import com.duangframework.mysql.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author laotang
 */
public class DBSession {

	private static final Logger logger = LoggerFactory.getLogger(DBSession.class);
	/** 每一个线程都有自己的连接 **/
	public static final ThreadLocal<Connection> connContainer = new ThreadLocal<>();
	public static final Object[] NULL_OBJECT = new Object[0];
		
	private static <T> T call(DBAction<T> dbAction){
		T result = null;
		Connection connection = null; 
		try{
			connection  = getConnection();
			if(null == connection){ throw new MysqlException("connection is null");}
			DBRunner dbRunner = new DBRunner(connection);
			result = dbAction.execute(dbRunner);
		} catch(Exception e){
			 throw new MysqlException(e.getMessage(), e);
		} finally {
			if(ToolsKit.isNotEmpty(connection)){
				close(connection);
			}
		}
		return result;
	}

	/**
	 * 取出指定库里所有的表
	 * @return		表名集合
	 */
	public static  List<String> getMysqlTables(){
		return call(new DBAction<List<String>>(){
			@Override
			public List<String> execute(DBRunner dbRunner) throws SQLException {
				String sql = "select table_name from information_schema.tables where table_schema=?";
				Object[] params = {dbRunner.getConnection().getCatalog()};
				List<Map<String,Object>> queryList =  dbRunner.query(sql, params);
				List<String> resultList = MysqlUtils.toList(queryList);
				return resultList;
			}
		});
	}
	
	/**
	 * 根据表名取出所有索引
	 * @param tableName		表名
	 * @return		索引数组集合
	 */
	public static List<String> getIndexs(final String tableName) {
		return call(new DBAction<List<String> >(){
			@Override
			public List<String> execute(DBRunner dbRunner) throws SQLException {
				String sql = "show index from "+ tableName;
				String[] filterNames = {"Key_name"};
				List<Map<String,Object>> queryList = dbRunner.query(sql, filterNames, NULL_OBJECT);
				List<String> resultList = MysqlUtils.toList(queryList);
				return resultList;
			}
		});
	}
	
	/**
	 * 执行查询SQL语句
	 * @param querySql		查询SQL语句
	 * @param params			参数数组
	 * @return
	 */
	public static List<Map<String,Object>> query(final String querySql, final Object... params){
		return call(new DBAction<List<Map<String,Object>>>(){
			@Override
			public List<Map<String,Object>> execute(DBRunner dbRunner) throws SQLException {
				return  dbRunner.query(querySql, params);
			}
		});
	}
	
	/**
	 *  执行SQL语句，用于update, delete等
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int execute(final String sql, final Object... params)  {
		return call(new DBAction<Integer>(){
			@Override
			public Integer execute(DBRunner dbRunner) throws SQLException {
				return dbRunner.execute(sql, params);
			}
		});
	}
	

	private static Connection getConnection(){
		Connection connection = connContainer.get();
		// 如果在当前线程里不存在连接，则重新取
		if(null == connection){
			try {
				connection = MysqlUtils.getDataSource().getConnection();
			} catch (Exception e) {
				throw new MysqlException("connection is null");
			}
			if(null != connection) {
				connContainer.set(connection);
			}
		}
		return connection;
	}
	
	/**
	 * 开启事务
	 */
	public static void startTransaction() {
		Connection connection = getConnection(); 
		try {
			if(null != connection){
				connection.setAutoCommit(false);
			}
		} catch (SQLException e) {
			throw new MysqlException("StartTransaction is Error: "+e.getMessage(), e);
		}
	}

	/**
	 * 提交事务
	 */
	public static void commintTransaction() {
		Connection connection = getConnection(); 
		try {
			if(null != connection){
				connection.commit();
			}
		} catch (SQLException e) {
			throw new MysqlException("CommintTransaction is Error: " + e.getMessage(), e);
		} finally{
			close(connection);
		}
	}
	
	/**
	 * 回滚事务
	 */
	public static void rollbakcTransaction() {
		Connection connection = getConnection(); 
		try {
			if(null != connection){
				connection.rollback();
			}
		} catch (SQLException e) {
			logger.warn("RollbakcTransaction is Error: " + e.getMessage(), e);
		} finally{
			close(connection);
		}
	}
	
	
	public static void close(PreparedStatement stmt) {
		if(ToolsKit.isNotEmpty(stmt)){
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
	public static void close(Connection conn) {
		if(ToolsKit.isNotEmpty(conn)){
			try {
				conn.close();
				connContainer.remove();
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}
	
}