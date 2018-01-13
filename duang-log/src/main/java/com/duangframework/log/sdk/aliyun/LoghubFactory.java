package com.duangframework.log.sdk.aliyun;

import com.aliyun.openservices.log.producer.LogProducer;
import com.aliyun.openservices.log.producer.ProjectConfig;
import com.duangframework.log.utils.PropertieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by laotang on 2017/3/8.
 */
public class LoghubFactory {

    private static final Logger logger = LoggerFactory.getLogger(LoghubFactory.class);
    private static ReentrantLock _loghubFactoryLock = new ReentrantLock();
    private static LogProducer slsProducer;

    public static LogProducer getInstance()  {
        if (null ==slsProducer) {
            try {
                _loghubFactoryLock.lock();
                //使用默认配置创建 producer 实例
                slsProducer = new LogProducer(new SLSProducerConfig());
                buildProducerConfig();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                throw new NullPointerException("slsProducer is null: " + e.getMessage());
            } finally {
                _loghubFactoryLock.unlock();
            }
        }
        return slsProducer;
    }

    // 添加多个 project 配置
    private static void buildProducerConfig() throws Exception{
        if (null ==slsProducer) {
            throw new NullPointerException("slsProducer is null");
        }
        String[] projectArray = PropertieUtil.getPorjects();
        for(String project : projectArray) {
            ProjectConfig projectConfig = new ProjectConfig(project, PropertieUtil.getEndPoint(), PropertieUtil.getAccessKey(), PropertieUtil.getAccessKeySecret());
            slsProducer.setProjectConfig(projectConfig);
        }
    }


}
