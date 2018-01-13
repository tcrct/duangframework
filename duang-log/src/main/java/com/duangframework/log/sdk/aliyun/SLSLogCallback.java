package com.duangframework.log.sdk.aliyun;

import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.producer.ILogCallback;
import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.response.PutLogsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

/**
 * 日志发送回调
 * 主要用于日志发送结果的处理，结果包括发送成功和发生异常。
 * Created by laotang on 2017/3/8.
 */
public class SLSLogCallback extends ILogCallback {

    private static final Logger logger = LoggerFactory.getLogger(SLSLogCallback.class);

    //保存要发送的数据，当时发生异常时，进行重试
    private String project;
    private String logstore;
    private String topic;
    private String shardHash;
    private String source;
    private Vector<LogItem> items;
    private LogProducer producer;
    private int retryTimes = 0;

    public SLSLogCallback(String project, String logstore, String topic,
                          String shardHash, String source, Vector<LogItem> items, LogProducer producer) {
        super();
        this.project = project;
        this.logstore = logstore;
        this.topic = topic;
        this.shardHash = shardHash;
        this.source = source;
        this.items = items;
        this.producer = producer;
    }


    @Override
    public void onCompletion(PutLogsResponse response, LogException e) {
        if (e != null) {
            // 打印异常
            logger.warn("###########SLSLogCallback: " + e.GetErrorCode() + ", " + e.GetErrorMessage() + ", " + e.GetRequestId());
            //最多重试三次
            if (retryTimes++ < SLSProducerConfig.retryTimes) {
                producer.send(project, logstore, topic, source, shardHash, items, this);
            }
        }
//        else {
//            System.out.println("send success, request id: " + response.GetRequestId());
//        }
    }
}