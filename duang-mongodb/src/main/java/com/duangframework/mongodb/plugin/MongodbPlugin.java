package com.duangframework.mongodb.plugin;

import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.mongodb.common.MongoConnect;
import com.duangframework.mongodb.kit.MongoKit;

/**
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongodbPlugin implements IPlugin {


    @Override
    public void start() throws Exception {
        MongoKit.duang().connect(new MongoConnect(
                ConfigKit.duang().key("mongodb.host").defaultValue("127.0.0.1").asString(),
                ConfigKit.duang().key("mongodb.port").defaultValue("27017").asInt(),
                ConfigKit.duang().key("mongodb.databasename").defaultValue("local").asString(),
                ConfigKit.duang().key("mongodb.username").defaultValue("").asString(),
                ConfigKit.duang().key("mongodb.password").defaultValue("").asString(),
                ConfigKit.duang().key("mongodb.replicaset").asList()
        )).getClient();
    }

    @Override
    public void stop() throws Exception {

    }
}
