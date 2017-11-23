package com.duangframework.mongodb.kit;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.common.MongoConnect;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoClientKit {

    private static Logger logger = LoggerFactory.getLogger(ConfigKit.class);

    private static MongoClientKit _mongoClientKit;
    private static Lock _mongoClientKitLock = new ReentrantLock();
    private static MongoConnect _mongoConnect;
    private static MongoClient _mongoClient;
    private static List<ServerAddress> _hostList = new ArrayList<>();
    private static List<MongoCredential> _authList = new ArrayList<>();
    private static MongoClientOptions.Builder _options;				// mongodb参数设置

    public static MongoClientKit duang() {
        if(null == _mongoClientKit) {
            try {
                _mongoClientKitLock.lock();
                _mongoClientKit = new MongoClientKit();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                _mongoClientKitLock.unlock();
            }
        }
        clear();
        return _mongoClientKit;
    }

    private static void clear() {
        _hostList.clear();
        _authList.clear();
    }

    private MongoClientKit() {
    }

    public MongoClientKit connect(MongoConnect connect) {
        _mongoConnect = connect;
       _options = MongoClientOptions.builder()
                .connectionsPerHost(100)									// 最大连接数
                .minConnectionsPerHost(10)
                .heartbeatFrequency(10000)
                .minHeartbeatFrequency(500)
                .heartbeatConnectTimeout(20000)
                .heartbeatSocketTimeout(20000)
                .localThreshold(15)
                .readPreference(ReadPreference.secondaryPreferred())		//优先在从节点中读数据,从节点有异常时再从主节点读数据
                .connectTimeout(10000)
                .maxWaitTime(20000)
                .socketTimeout(10000) //10S
                .threadsAllowedToBlockForConnectionMultiplier(5);			// 与connectionsPerHost相乘，变成一个线程变为可用的最大阻塞数
        return this;
    }


    // 数据库授权
    private void auth() {
        if(ToolsKit.isNotEmpty(_mongoConnect.getUserName()) && ToolsKit.isNotEmpty(_mongoConnect.getPassWord()) ) {
            _authList.add(MongoCredential.createScramSha1Credential(
                    _mongoConnect.getUserName(),
                    _mongoConnect.getDataBase(),
                    _mongoConnect.getPassWord().toCharArray()));
        }
    }

    private void hosts() {
        if( ToolsKit.isNotEmpty(_mongoConnect.getRepliCaset()) ) {
            for(String replicasetString : _mongoConnect.getRepliCaset()) {
                String[] replicasetItemArray = replicasetString.split(":");
                if(ToolsKit.isEmpty(replicasetItemArray) || replicasetItemArray.length != 2){
                    throw new RuntimeException("replicasetItemArray is null or length != 2 ");
                }
                _hostList.add(new ServerAddress(replicasetItemArray[0], Integer.parseInt(replicasetItemArray[1])));
                logger.info("connect replicaset mongodb host: " + replicasetItemArray[0]+"           port: "+ replicasetItemArray[1]);
            }
        } else {
            if(ToolsKit.isNotEmpty(_mongoConnect.getHost()) && _mongoConnect.getPort()>-1) {
                _hostList.add(new ServerAddress(_mongoConnect.getHost(), _mongoConnect.getPort()));
                logger.info("connect single mongodb host: " + _mongoConnect.getHost()+"           port: "+ _mongoConnect.getPort());
            }
        }

        if(ToolsKit.isEmpty(_hostList)) {
            throw new EmptyNullException("connect mongdb, host and port is null or empty");
        }
    }

    public MongoClient getClient() {
        if(ToolsKit.isEmpty(_mongoClient)) {
            try {
                hosts();
                auth();
                _mongoClient = new MongoClient(_hostList, _authList, _options.build());
             } catch (Exception e) {
                throw new RuntimeException("Can't connect mongodb!");
            }
        }
        return _mongoClient;
    }

    public DB getDB(String dbName) {
        return getClient().getDB(dbName);
    }

    public DB getDefaultDB() {
        return getClient().getDB(_mongoConnect.getDataBase());
    }

    public MongoDatabase getMongoDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public MongoDatabase getDefaultMongoDatabase() {
        return getClient().getDatabase(_mongoConnect.getDataBase());
    }

}
