package com.duangframework.mysql.utils;

import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mysql.common.IConnect;
import com.duangframework.mysql.common.MySqlConnect;
import com.duangframework.mysql.core.ds.DruidDataSourceFactory;
import com.duangframework.mysql.core.ds.IDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.*;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    private static DataSource dataSource = null;

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

    public static DataSource getDataSource() {
        IConnect connect = new MySqlConnect(
                ConfigKit.duang().key("mysql.host").defaultValue("127.0.0.1").asString(),
                ConfigKit.duang().key("mysql.port").defaultValue("3306").asInt(),
                ConfigKit.duang().key("mysql.jdbc.url").defaultValue("").asString()
        );
        return getDataSource(connect);
    }

    public static DataSource getDataSource(IConnect connect) {
        if (ToolsKit.isNotEmpty(dataSource)) {
            IDataSourceFactory dsFactory = null;
            String dataSourceFactoryClassName = ConfigKit.duang().key("mysql.datasource").asString();
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
        }
        return dataSource;
    }
}
