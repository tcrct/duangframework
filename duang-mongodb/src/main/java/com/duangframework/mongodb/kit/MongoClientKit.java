package com.duangframework.mongodb.kit;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.MongodbException;
import com.duangframework.core.interfaces.IConnect;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.mongodb.common.MongoDbConnect;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongoClientKit {

    private static Logger logger = LoggerFactory.getLogger(MongoClientKit.class);

    private MongoDbConnect _mongoDbConnect;
    private MongoClient _mongoClient;
    private List<ServerAddress> _hostList = new ArrayList<>();
    private List<MongoCredential> _authList = new ArrayList<>();
    private MongoClientOptions.Builder _options;				// mongodb参数设置

    public static MongoClientKit duang() {
        return new MongoClientKit();
    }

    private MongoClientKit() {
    }

    public MongoClientKit connect(IConnect connect) {
        _mongoDbConnect = (MongoDbConnect) connect;
        return this;
    }

    private void options() {
        _options = MongoClientOptions.builder();
//                .connectionsPerHost(100)									// 最大连接数
//                .minConnectionsPerHost(10)
//                .heartbeatFrequency(10000)
//                .minHeartbeatFrequency(500)
//                .heartbeatConnectTimeout(20000)
//                .heartbeatSocketTimeout(20000)
//                .localThreshold(15)
//                .readPreference(ReadPreference.secondaryPreferred())		//优先在从节点中读数据,从节点有异常时再从主节点读数据
//                .connectTimeout(10000)
//                .maxWaitTime(20000)
//                .socketTimeout(10000) //10S
//                .threadsAllowedToBlockForConnectionMultiplier(5);			// 与connectionsPerHost相乘，变成一个线程变为可用的最大阻塞数
    }


    // 数据库授权
    private void auth() {
        if(ToolsKit.isNotEmpty(_mongoDbConnect.getUserName()) && ToolsKit.isNotEmpty(_mongoDbConnect.getPassWord()) ) {
            _authList.add(MongoCredential.createScramSha1Credential(
                    _mongoDbConnect.getUserName(),
                    _mongoDbConnect.getDataBase(),
                    _mongoDbConnect.getPassWord().toCharArray()));
        }
    }

    private void hosts() {
        if( ToolsKit.isNotEmpty(_mongoDbConnect.getRepliCaset()) ) {
            for(String replicasetString : _mongoDbConnect.getRepliCaset()) {
                String[] replicasetItemArray = replicasetString.split(":");
                if(ToolsKit.isEmpty(replicasetItemArray) || replicasetItemArray.length != 2){
                    throw new RuntimeException("replicasetItemArray is null or length != 2 ");
                }
                _hostList.add(new ServerAddress(replicasetItemArray[0], Integer.parseInt(replicasetItemArray[1])));
                logger.warn("connect replicaset mongodb host: " + replicasetItemArray[0]+"           port: "+ replicasetItemArray[1]);
            }
        } else {
            if(ToolsKit.isNotEmpty(_mongoDbConnect.getHost()) && _mongoDbConnect.getPort()>-1) {
                _hostList.add(new ServerAddress(_mongoDbConnect.getHost(), _mongoDbConnect.getPort()));
                logger.warn("connect single mongodb host: " + _mongoDbConnect.getHost()+"           port: "+ _mongoDbConnect.getPort());
            }
        }

        if(ToolsKit.isEmpty(_hostList)) {
            throw new EmptyNullException("connect mongdb, host and port is null or empty");
        }
    }

    public MongoClient createMongoDBClientWithOutURI() throws Exception {
        if(ToolsKit.isEmpty(_mongoClient)) {
            try {
                options();
                hosts();
                auth();
                _mongoClient = new MongoClient(_hostList, _authList, _options.build());
            } catch (Exception e) {
                throw new RuntimeException("Can't connect mongodb: " + e.getMessage() , e);
            }
        }
        return _mongoClient;
    }

    public MongoClient createMongoDBClientWithURI() throws Exception {
        MongoClientURI connectionString = new MongoClientURI(_mongoDbConnect.getUrl());
        logger.warn("mongodb connection url: " + connectionString);
        _mongoClient = new MongoClient(connectionString);
        if(null != _mongoClient){
            logger.warn("Connection ReplicaSet Mongodb Success...");
        }else{
            throw new NullPointerException("can't connect mongodb database! crate client fail");
        }
        return _mongoClient;
    }

    public MongoClient getClient() {
        try {
            if (ToolsKit.isEmpty(_mongoDbConnect.getUrl())) {
                return createMongoDBClientWithOutURI();
            }
            return createMongoDBClientWithURI();
        } catch (Exception e) {
            throw new MongodbException(e.getMessage(), e);
        }
    }

    public DB getDB(String dbName) {
        return getClient().getDB(dbName);
    }

    public DB getDefaultDB() {
        return getClient().getDB(_mongoDbConnect.getDataBase());
    }

    public MongoDatabase getMongoDatabase(String dbName) {
        return getClient().getDatabase(dbName);
    }

    public MongoDatabase getDefaultMongoDatabase() {
        return getClient().getDatabase(_mongoDbConnect.getDataBase());
    }

}
