package com.duangframework.mysql.utils;

import com.duangframework.core.annotation.db.Index;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.kit.ObjectKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mysql.MysqlDao;
import com.duangframework.mysql.common.CurdEnum;
import com.duangframework.mysql.common.CurdSqlModle;
import com.duangframework.mysql.common.MysqlClientExt;
import com.duangframework.mysql.common.MysqlDbConnect;
import com.duangframework.mysql.core.DBSession;
import com.duangframework.mysql.core.ds.DruidDataSourceFactory;
import com.duangframework.mysql.core.ds.IDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by laotang on 2017/11/25 0025.
 */
public class MysqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    private static Map<String, MysqlClientExt> dataSourceMap = new HashMap<>();
    private static Map<String, Set<String>> ALL_TABLES = new HashMap<>();
    private static String defualExampleCode;
    private static final Object[] NULL_OBJECT = new Object[0];
    private static ConcurrentMap<String, MysqlDao<?>> MYSQLDAO_MAP = new ConcurrentHashMap<>();
    private static MysqlClientExt mysqlClientExt;

    public static String getDefualExampleCode() {
        return defualExampleCode;
    }

    public static MysqlClientExt getDefaultClientExt() {
        return mysqlClientExt;
    }
    public static void setDefaultClientExt(MysqlClientExt defaultClient) {
        MysqlUtils.mysqlClientExt = defaultClient;
    }
    public static void setMysqlClient(String key, MysqlClientExt client) {
        dataSourceMap.put(key, client);
    }
    public static MysqlClientExt getMysqlClientMap(String key) {
        return dataSourceMap.get(key);
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

    public static Connection getConnection(String key) throws Exception {
        if(dataSourceMap.isEmpty()) {
          throw new EmptyNullException("请先启动MysqlPlugin插件");
        }
        MysqlClientExt clientExt = getMysqlClientMap(key);
        if(null != clientExt && ToolsKit.isNotEmpty(clientExt.getClient())) {
            return clientExt.getClient().getConnection();
        } else {
            return getDataSource(clientExt.getConnect()).getConnection();
        }
    }

    public static DataSource getDataSource(MysqlDbConnect connect) {
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

    /**
     * 取表索引
     * @param dataBase         数据库名
     * @param entityClass         entity类
     * @return
     */
    public static List<String> getIndexs(String dataBase, Class<?> entityClass) {
        String tableName = ClassUtils.getEntityName(entityClass, true);
        List<String> indexList = DBSession.getIndexs(dataBase, tableName);
        return ToolsKit.isEmpty(indexList) ? null : indexList;
    }

    /**
     * 取出数据库所有表,加载到Map
     */
    public static void getAllTable(String database) throws Exception {
        List<String> list = DBSession.getMysqlTables(database);
        if(ToolsKit.isNotEmpty(list)){
            ALL_TABLES.get(database).addAll(list);
        }
    }

    /**
     * 是否存在表
     *
     * @param cls
     *            Entiey类
     * @return 存在返回true, 否则反之
     */
    public static boolean isExist(String database, Class<? extends IdEntity> cls) throws Exception {
        String tableName = ClassUtils.getEntityName(cls,true);
        Set<String> tableNameSet = ALL_TABLES.get(database);
        if(ToolsKit.isEmpty(tableNameSet)) {
            logger.warn("get "+ database +" table is empty!" );
            return false;
        }
        return tableNameSet.contains(tableName);
    }

    /**
     * 创建表
     * @param databaseName
     * @param tableName
     * @param entityClass
     */
    public static void createTables(String databaseName, String tableName, Class<?> entityClass ) {

    }

    /**
     * 创建索引
     * @param databaseName
     * @param tableName
     * @param entityClass
     */
    public static void createIndexs(String databaseName, String tableName, Class<?> entityClass ) {
        // 先去查表里已经存在的索引
        List<String> indexs = MysqlUtils.getIndexs(databaseName, entityClass);
        Field[] fields = ClassUtils.getFields(entityClass);
        if (ToolsKit.isEmpty(fields)) {
            return;
        }
        StringBuilder indexSql = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            Index index = fields[i].getAnnotation(Index.class);
            String columnName = ToolsKit.getFieldName(fields[i]);
            if (ToolsKit.isNotEmpty(index)) {
                indexSql.delete(0,indexSql.length());
                String indexName = ToolsKit.isEmpty(index.name()) ? "_" + columnName + "_" : index.name();
                indexName = indexName.toLowerCase();
                //如果不存在则添加，存在则不作任何处理
                if(ToolsKit.isNotEmpty(indexs) && !indexs.contains(indexName)){
                    boolean unique = index.unique();
                    String order = ToolsKit.isEmpty(index.order()) ? "asc" : index.order();
                    indexSql.append("create ");
                    if(unique) {
                        indexSql.append("unique ");
                    }
                    indexSql.append(" index ").append(indexName).append(" on ").append(tableName).append("(").append(columnName);
                    if("desc".equalsIgnoreCase(order)){
                        indexSql.append(" ").append(order);
                    }
                    indexSql.append(");");
                    try {
                        System.out.println("indexSql: " + indexSql.toString());
//                        DBSession.execute(databaseName, indexSql.toString(), NULL_OBJECT);
                    } catch (Exception e) {
                        logger.warn("create["+databaseName+"."+tableName+"."+columnName+"] index["+indexName+"] is fail: " + e.getMessage(), e);
                    }
                }
            }
        }
    }



    /**
     * 根据Entity类取出MongoDao
     * @param cls           继承了IdEntity的类
     * @param <T>
     * @return
     */
    public static <T> MysqlDao<T> getMysqlDao(String dbClientCode, Class<T> cls){
        String key = ClassUtils.getEntityName(cls);
        key = ToolsKit.isNotEmpty(dbClientCode) ? dbClientCode+"_" + key : key;
        MysqlDao<?> dao = MYSQLDAO_MAP.get(key);
        if(null == dao){
            MysqlClientExt clientExt = null;
            if(ToolsKit.isNotEmpty(dbClientCode)) {
                clientExt = MysqlUtils.getMysqlClientMap(dbClientCode);
            } else {
                clientExt = MysqlUtils.getDefaultClientExt();
            }
            if(ToolsKit.isEmpty(clientExt)) {
                throw new MysqlException("mysql client  is null");
            }
            dao = new MysqlDao<T>(clientExt.getConnect().getClientCode(), cls);
            MYSQLDAO_MAP.put(key, dao);
        }
        return (MysqlDao<T>)dao;
    }

    public static CurdSqlModle builderSqlModle(CurdEnum curdEnum, Class<?> entityClass, Map<String, Object> paramMap, String idFieldName) {

        String tableName = ClassUtils.getEntityName(entityClass);
        CurdSqlModle modle = null;
        if(ToolsKit.isNotEmpty(tableName) && ToolsKit.isNotEmpty(paramMap)) {
            modle = new CurdSqlModle(curdEnum, tableName, paramMap, idFieldName);
        }
        return modle;
    }

    /**
     * 排序
     */
    public static List<String> orderParamKey(Map<String,?> paramMap) {
        if (ToolsKit.isEmpty(paramMap)) {
            return null;
        }
        ArrayList<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList, String.CASE_INSENSITIVE_ORDER);
        return keyList;
    }
}
