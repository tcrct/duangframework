package com.duangframework.mysql.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author laotang
 */
public interface DBAction<T> {
	
	T execute(DBRunner dbRunner) throws SQLException;
}
