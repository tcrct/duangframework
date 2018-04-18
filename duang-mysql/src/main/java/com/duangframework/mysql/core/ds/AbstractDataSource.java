/*
 * 数据链接池抽象类
 */
package com.duangframework.mysql.core.ds;

import com.duangframework.core.interfaces.IConnect;

import javax.sql.DataSource;

/**
 * 数据链接池抽象类，子类继承实现
 * @author laotang
 */
public abstract class AbstractDataSource<T extends DataSource> implements IDataSourceFactory{

	@Override
	public T getDataSource(IConnect connect) throws Exception {
		T ds = builderDataSource();
		setUsername(ds, connect.getUserName());
		setPassword(ds, connect.getPassWord());
		setUrl(ds, connect.getUrl());
		setInitParam(ds);
		return ds;
	}


	/**
	 *  构建数据源
	 * @return
	 */
	public abstract T builderDataSource();

	/**
	 *  设置用户名
	 * @param ds
	 * @param userName
	 */
	public abstract void setUsername(T ds, String userName);

	/**
	 *  设置密码
	 * @param ds
	 * @param password
	 */
	public abstract void setPassword(T ds, String password);

	/**
	 *  设置链接字符串
	 * @param ds
	 * @param jdbcUrl
	 */
	public abstract void setUrl(T ds, String jdbcUrl);

	/**
	 *  设置初始化参数值
	 * @param ds
	 * @param map
	 */
	public abstract void setInitParam(T ds);
	
}
