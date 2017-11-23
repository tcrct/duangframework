package com.duangframework.mongodb.kit;

import com.duangframework.core.common.IdEntity;
import com.duangframework.core.exceptions.MongodbException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoConnect;
import com.duangframework.mongodb.common.MongoQuery;
import com.duangframework.mongodb.utils.MongoUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoKit {

    private static Logger logger = LoggerFactory.getLogger(ConfigKit.class);

    private static MongoKit _mongoKit;
    private static Lock _mongoKitLock = new ReentrantLock();
    private static MongoClient _mongoClient;
    private static MongoClientKit _mongoClientKit;
    private static Class<?> _entityClass;
    private static MongoDatabase _database;
    private static MongoCollection _collection;
    private static MongoQuery mongoQuery;
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
    }

    private MongoKit() {
    }

    public MongoKit client(MongoConnect connect) {
        mongoClient = MongoClientKit.duang().connect(connect).getClient();
        return this;
    }

    public MongoKit use(Class<?> clazz) {
        _entityClass = clazz;
        return this;
    }

    public MongoKit eq(String key, Object value){
        mongoQuery.eq(key, value);
        return this;
    }

    public MongoKit entity(IdEntity entityObj) {
        _entityObj = entityObj;
        return this;
    }

    public boolean save() {
        getClient();
        try {
            MongoDao dao = getMongoDao();
            return dao.save(_entityObj);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    public <T> T findOne() {
        getClient();
        try {
            MongoDao<T> dao = getMongoDao();
            return dao.findOne(mongoQuery);
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    private <T> MongoDao<T> getMongoDao() {
        return (MongoDao<T>)MongoUtils.getMongoDao(_entityClass);
    }


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

    private DB getDB(String dbName) {
        return getClient().getDB(dbName);
    }

    private MongoDatabase getMongoDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

}
