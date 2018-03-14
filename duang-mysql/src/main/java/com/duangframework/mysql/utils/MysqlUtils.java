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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    private static DataSource dataSource = null;
    private static IConnect connect = null;

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

    public static DataSource getDataSource(String userNaem, String passWord, String jdbcUrl, String dataSourceFactoryClassName) throws Exception {
        if(null == connect) {
            connect = new MySqlConnect( userNaem, passWord,jdbcUrl,dataSourceFactoryClassName);
        }
        return getDataSource(connect);
    }

    public static DataSource getDataSource() throws Exception {
        if(null == connect) {
          throw new EmptyNullException("请先启动MysqlPlugin插件");
        }
        return getDataSource(connect);
    }

    public static DataSource getDataSource(IConnect connect) {
        if (ToolsKit.isEmpty(dataSource)) {
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
        }
        return dataSource;
    }
}
