package com.duangframework.log.sdk.aliyun;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.producer.LogProducer;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.log.core.ILog;
import com.duangframework.log.core.LogDto;
import com.duangframework.log.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * @author Created by laotang
 * @date createed in 2018/1/13.
 */
public class SLSLogClient implements ILog<LogDto>{

    private static Logger logger = LoggerFactory.getLogger(SLSLogClient.class);
    private static SLSLogClient ourInstance = new SLSLogClient();
    private static String _shardHost;		// 根据 hashkey 确定写入 shard，hashkey 可以是 MD5(ip) 或 MD5(id) 等
    private static Vector<LogItem> _logGroup = null;
    private static LogProducer _logProducer = null;
    private static String _source;

    public static SLSLogClient getInstance() {
        return ourInstance;
    }

    private SLSLogClient() {
        _shardHost = LogUtils.getLogShardHash();
        _logProducer = LoghubFactory.getInstance();
        _source = IpUtils.getLocalHostIP();
    }


    @Override
    public void submit(LogDto requestDto) throws Exception {
        if(ToolsKit.isEmpty(requestDto)) {
            throw  new EmptyNullException("LogRequest Dto is null");
        }
        _logGroup = new Vector<>();
        items(requestDto.getLogItemMap());
        String _project = requestDto.getProject();
        String _store = requestDto.getStore();
        String _topic = requestDto.getTopic();
        // 更牛B的发送方式
        try{
            _logProducer.send(_project, _store, _topic, _shardHost, _source, _logGroup,
                    new SLSLogCallback(_project, _store, _topic, _shardHost, _source, _logGroup, _logProducer));
        }catch (Exception e) {
            e.printStackTrace();
            logger.warn("_project:  " + _project);
            logger.warn("_store:  " + _store);
            logger.warn("_topic:  " + _topic);
            logger.warn("_shardHost:  " + _shardHost);
            logger.warn("_source:  " + _source);
            logger.warn("_logGroup:  " + ToolsKit.toJsonString(_logGroup));
            logger.warn("_logProducer:  " + _logProducer);
            logger.warn("SLSKit send if fail: " + e.getMessage(), e);
            throw  e;
        }

    }


    private void items(Map<String ,String> logItemMap) {
        if(ToolsKit.isNotEmpty(logItemMap)) {
            LogItem logItem = new LogItem((int) (System.currentTimeMillis() / 1000));
            for (Iterator<Map.Entry<String, String>> it = logItemMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if(ToolsKit.isNotEmpty(entry)) {
                    String value = entry.getValue();
                    logItem.PushBack(entry.getKey(), ToolsKit.isEmpty(value) ? "" : value);
                }
            }
            _logGroup.add(logItem);
        }
    }

}
