package com.duangframework.cache.core;

import redis.clients.jedis.*;

/**
 * @author Created by laotang
 * @date createed in 2018/1/19.
 */
public interface IJedisCache extends JedisCommands, MultiKeyCommands,
        AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands, SentinelCommands,
        MultiKeyJedisClusterCommands, JedisClusterScriptingCommands{
}
