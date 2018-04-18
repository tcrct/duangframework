package com.duangframework.mysql.core.ds;

import com.duangframework.core.interfaces.IConnect;

import javax.sql.DataSource;

/**
 * 数据源接口
 * @author laotang
 * @since 1.0
 */
public interface IDataSourceFactory {

	/**
	 *  获取数据源
	 * @return		DataSource
	 */
	DataSource getDataSource(IConnect connect) throws Exception;

}
