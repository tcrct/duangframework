package com.duangframework.mysql.utils;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mysql.common.IConnect;
import com.duangframework.mysql.common.MySqlConnect;
import com.duangframework.mysql.core.ds.DruidDataSourceFactory;
import com.duangframework.mysql.core.ds.IDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    private static Map<String, DataSource> dataSourceMap = new HashMap<>();
    private static IConnect connect = null;
    private static String defualDataBase;


    public static String getDefualDataBase() {
        return defualDataBase;
    }
    /**
     *
     * @param queryResultList
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T toList(List<Map<String,Object>> queryResultList) {
        if(ToolsKit.isEmpty(queryResultList)){
            logger.warn("list is null, returu null...");
            return null;
        }
        List<T>resultList = new ArrayList<T>(queryResultList.size());
        for(Iterator<Map<String,Object>> it = queryResultList.iterator(); it.hasNext();){
            Map<String,Object> map = it.next();
            if(ToolsKit.isEmpty(map)) {
                continue;
            }
            for(Iterator<Map.Entry<String,Object>> mapIt = map.entrySet().iterator(); mapIt.hasNext();){
                Map.Entry<String,Object> entry = mapIt.next();
                if(ToolsKit.isNotEmpty(entry)){
                    resultList.add((T)entry.getValue());
                }
            }
        }
        return (T)resultList;
    }

    public static void initDataSource(List<MySqlConnect> connectList) throws Exception {
        for(MySqlConnect connect : connectList) {
            if(ToolsKit.isEmpty(connect.getDataBase())) {
                throw new NullPointerException("database name is null");
            }
            DataSource dataSource = getDataSource(connect);
            if(null != dataSource) {
                dataSourceMap.put(connect.getDataBase(), dataSource);
//                dataSource.getConnection();
            }
        }
        if(ToolsKit.isNotEmpty(connectList)) {
            defualDataBase = connectList.get(0).getDataBase();
        }
    }

    public static Connection getConnection(String key) throws Exception {
        if(dataSourceMap.isEmpty()) {
          throw new EmptyNullException("请先启动MysqlPlugin插件");
        }
        return dataSourceMap.get(key).getConnection();
    }

    public static DataSource getDataSource(IConnect connect) {
        DataSource dataSource = null;
        IDataSourceFactory dsFactory = null;
        String dataSourceFactoryClassName = connect.getDataSourceFactoryClassName();
        if (ToolsKit.isEmpty(dataSourceFactoryClassName)) {
            dsFactory = ObjectKit.newInstance(DruidDataSourceFactory.class);
        } else {
            dsFactory = ObjectKit.newInstance(dataSourceFactoryClassName);
        }
        try {
            dataSource = dsFactory.getDataSource(connect);
        } catch (Exception e) {
            throw new MysqlException(e.getMessage(), e);
        }

        return dataSource;
    }
}
