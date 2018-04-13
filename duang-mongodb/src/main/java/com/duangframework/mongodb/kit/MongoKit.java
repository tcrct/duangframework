package com.duangframework.mongodb.kit;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.common.dto.result.PageDto;
import com.duangframework.core.exceptions.MongodbException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoConnect;
import com.duangframework.mongodb.common.MongoQuery;
import com.duangframework.mongodb.common.MongoUpdate;
import com.duangframework.mongodb.utils.MongoUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoKit {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoKit.class);

    static {
//        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.WARN);
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
    }

    private static MongoKit _mongoKit;
    private static Lock _mongoKitLock = new ReentrantLock();
    private static MongoClient _mongoClient;
    private static MongoClientKit _mongoClientKit;
    private static Class<?> _entityClass;
    private static MongoDatabase _database;
    private static MongoCollection _collection;
    private static MongoQuery mongoQuery;
    private static MongoUpdate mongoUpdate;
    private static MongoClient mongoClient;
    private static IdEntity _entityObj;

    public static MongoKit duang() {
        if(null == _mongoKit) {
            try {
                _mongoKitLock.lock();
                _mongoKit = new MongoKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _mongoKitLock.unlock();
            }
        }
        clear();
        return _mongoKit;
    }

    private static void clear() {
        mongoQuery = new MongoQuery();
        mongoUpdate = new MongoUpdate();
    }

    private MongoKit() {
    }

    /**
     * 根据connect对象取得MongoClient
     * @param connect       @MongoConnect对象
     * @return
     */
    public MongoKit client(MongoConnect connect) {
        mongoClient = MongoClientKit.duang().connect(connect).getClient();
        return this;
    }

    /**
     * 指定要操作的实体类
     * @param clazz     继承IdEntity的实体类
     * @return
     */
//    public MongoKit use(Class<?> clazz) {
//        _entityClass = clazz;
//        return this;
//    }

    public <T> MongoKit use(Class<T> clazz) {
        _entityClass = clazz;
        return this;
    }

    public MongoKit querybson(MongoQuery query){
        mongoQuery = query;
        return this;
    }

    public MongoKit updatebson(MongoUpdate update){
        mongoUpdate = update;
        return this;
    }

    /**
     * 要新增或更新且继承了IdEntity的实体类
     * @param entityObj     实体类
     * @return
     */
    public MongoKit entity(IdEntity entityObj) {
        _entityObj = entityObj;
        return this;
    }

    /**
     * 保存记录
     * 根据entity()方法里的entityObj里是否有id值来确定是新增还是更新，若id则视为更新
     * @return  保存成功返回true
     */
    public boolean save() {
        getClient();
        try {
            MongoDao dao = getMongoDao();
            return dao.save(_entityObj);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 查找多条记录，以Page对象返回
     * @param <T>       对象泛类
     * @return                泛型对象集合
     */
    public long update() {
        getClient();
        try {
            MongoDao dao = getMongoDao();
            return dao.update(mongoQuery, mongoUpdate);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 新增记录时，必须要保证有ID值
     * @return                泛型对象集合
     */
    public boolean insert() {
        getClient();
        try {
            MongoDao dao = getMongoDao();
            return dao.insert(_entityObj);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 查找一条记录
     * @param <T>       对象泛类
     * @return                泛型对象
     */
    public <T> T first() {
        getClient();
        try {
            MongoDao<T> dao = getMongoDao();
            return dao.findOne(mongoQuery);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 查找多条记录
     * @param <T>       对象泛类
     * @return                泛型对象集合
     */
    public <T> List<T> findList() {
        getClient();
        try {
            MongoDao<T> dao = getMongoDao();
            return dao.findList(mongoQuery);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    /**
     * 查找多条记录，以Page对象返回
     * @param <T>       对象泛类
     * @return                泛型对象集合
     */
    public <T> PageDto<T> findPage() {
        getClient();
        try {
            MongoDao<T> dao = getMongoDao();
            return dao.findPage(mongoQuery);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }



    /**
     * 取MongoDao类
     * @param <T>
     * @return
     */
    private <T> MongoDao<T> getMongoDao() {
        return (MongoDao<T>)MongoUtils.getMongoDao(_entityClass);
    }


    /**
     * 取MongoClient
     * @return
     */
    private MongoClient getClient() {
        if(ToolsKit.isEmpty(mongoClient)) {
            //框架启动后会有值
            mongoClient = MongoClientKit.duang().getClient();
            if(ToolsKit.isEmpty(mongoClient)) {
                // 取duang.properties指定的值
                mongoClient = MongoClientKit.duang().connect(new MongoConnect()).getClient();
            }
        }
        return mongoClient;
    }

    /**
     * 取Mongodb数据库， 3.x版前用
     * @param dbName        数据库名称
     * @return
     */
    private DB getDB(String dbName) {
        return getClient().getDB(dbName);
    }

    /**
     * 取Mongodb数据库， 3.x版后用
     * @param dbName        数据库名称
     * @return
     */
    private MongoDatabase getMongoDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

}
