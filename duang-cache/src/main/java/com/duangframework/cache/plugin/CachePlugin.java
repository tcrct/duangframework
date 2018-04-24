package com.duangframework.cache.plugin;

import com.duangframework.cache.common.CacheClientExt;
import com.duangframework.cache.common.CacheDbConnect;
import com.duangframework.cache.kit.CacheClientKit;
import com.duangframework.cache.utils.CacheUtils;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ToolsKit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by laotang
 * @date createed in 2018/1/17.
 */
public class CachePlugin implements IPlugin {

    private boolean isClusterRedis = false;
    private List<CacheDbConnect> connectList = new ArrayList<>();

    public CachePlugin(CacheDbConnect cacheDbConnect) {
        connectList.add(cacheDbConnect);
    }

    public CachePlugin(List<CacheDbConnect> cacheDbConnects) {
        connectList.addAll(cacheDbConnects);
    }

    public CachePlugin(String host, int port, String userName, String pwd, int database, String clientcode) {
        CacheDbConnect cacheDbConnect = new CacheDbConnect(host, port, database+"", userName, pwd, clientcode);
        connectList.add(cacheDbConnect);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start() throws Exception {

        boolean isFirstClient = true;
        for(CacheDbConnect connect : connectList) {
            String key = connect.getClientCode();
            CacheClientExt clientExt = null;
            if(connect.getHost().contains(",")) {
                Jedis jedis  = CacheClientKit.duang().connect(connect).getJedis();
                if(null != jedis) {
                    clientExt = new CacheClientExt(key, jedis, connect);
                }
            } else {
                JedisCluster clusterJedis  = CacheClientKit.duang().connect(connect).getClusterJedis();
                if(null != clusterJedis) {
                    clientExt = new CacheClientExt(key, clusterJedis, connect);
                }
            }
            if(ToolsKit.isNotEmpty(clientExt) && ToolsKit.isNotEmpty(key)) {
                if(isFirstClient) {
                    CacheUtils.setDefaultClientExt(clientExt);
                    isFirstClient = false;
                }
                if(connect.isDefaultClient()) {
                    CacheUtils.setDefaultClientExt(clientExt);
                }
                CacheUtils.setCacheClientExt(key, clientExt);
                connect.printDbInfo(key);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        CacheUtils.close();
    }
}
