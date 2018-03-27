package com.duangframework.mysql.plugin;


import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.exceptions.MvcStartUpException;
import com.duangframework.core.exceptions.MysqlException;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mysql.MysqlDao;
import com.duangframework.mysql.common.IMySql;
import com.duangframework.mysql.common.MySqlConnect;
import com.duangframework.mysql.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mysql 插件
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MysqlPlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(MysqlPlugin.class);
    private List<MySqlConnect> connectList = new ArrayList<>();

    /**
     * 单数据库时使用
     * @param host
     * @param port
     * @param userName
     * @param passWord
     * @param dataBase
     */
    public MysqlPlugin(String host, int port, String userName, String passWord,String dataBase) {
        MySqlConnect connect = new MySqlConnect(host, port, userName, passWord, dataBase);
        connectList.add(connect);
    }

    /**
     * 多数据库时使用
     * @param connectList
     */
    public MysqlPlugin(List<MySqlConnect> connectList) {
        this.connectList.addAll(connectList);
    }

    /**
     * 多数据库时使用
     * @param model
     */
    public MysqlPlugin(IMySql model) {
        try {
            this.connectList.addAll(model.builderConnects());
        } catch (Exception e) {
            throw new MvcStartUpException("start MysqlPlugin is fail: " + e.getMessage(), e);
        }
    }

    @Override
    public void init() throws Exception {
        // 可以初始一些值，框架先执行init方法后再执行start
    }

    @Override
    public void start() throws Exception {
        try {
            MysqlUtils.initDataSource(connectList);
            importDao();
        } catch (Exception e) {
            throw new MysqlException("connection is fail: " + e.getMessage(), e);
        }
//        // 创建表与索引
//        final Map<Class<?>, Object> entityMap = BeanUtils.getAllBeanMaps().get(Entity.class.getSimpleName());
//        if(ToolsKit.isEmpty(entityMap)) {
//            return;
//        }



//        ThreadPoolKit.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    for (Iterator<Class<?>> iterator = entityMap.keySet().iterator(); iterator.hasNext(); ) {
//                        Class<? extends IdEntity> entityClass = (Class<? extends IdEntity>) iterator.next();
//                        String databaseName = MysqlUtils.getDataBaseName(entityClass);
//                        String tableName = ClassUtils.getEntityName(entityClass);
//                        // 表
//                        MysqlUtils.createTables(databaseName, tableName, entityClass);
//                        // 索引
//                        MysqlUtils.createIndexs(databaseName, tableName, entityClass);
//                    }
//                } catch (Exception e) {
//                    logger.warn(e.getMessage(), e);
//                }
//            }
//        });

    }

    @Override
    public void stop() throws Exception {

    }

    /**
     * IOC注入MongoDao
     * @throws Exception
     */
    private void importDao() throws Exception {
        // 取出所有类对象
        Map<Class<?>, Object> allBeanMap = BeanUtils.getAllBeanMap();
        for(Iterator<Map.Entry<Class<?>, Object>> it = allBeanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class<?>, Object> entry = it.next();
            Class<?> beanClass = entry.getKey();
            Field[] fields = beanClass.getDeclaredFields();
            Object beanObj = entry.getValue();
            for(Field field : fields) {
                if (field.isAnnotationPresent(Import.class) && MysqlDao.class.equals(field.getType())) {
                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(types)) {
                        String paramTypeClassName = types[0].toString().substring(6).trim();
                        Class<?> paramTypeClass  = ClassUtils.loadClass(paramTypeClassName, false);
                        Object daoObj = MysqlUtils.getMysqlDao(paramTypeClass);
                        field.setAccessible(true);
                        field.set(beanObj, daoObj);
                    }
                }
            }
        }
    }
}
