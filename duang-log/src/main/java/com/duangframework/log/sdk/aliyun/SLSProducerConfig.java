package com.duangframework.log.sdk.aliyun;

import com.aliyun.openservices.log.producer.ProducerConfig;

/**
 * Created by laotang on 2017/3/8.
 */
public class SLSProducerConfig extends ProducerConfig {
    //被缓存起来的日志的发送超时时间，如果缓存超时，则会被立即发送，单位是毫秒
    public int packageTimeoutInMS = 3000;
    //每个缓存的日志包中包含日志数量的最大值，不能超过 4096
    public int logsCountPerPackage = 4096;
    //每个缓存的日志包的大小的上限，不能超过 5MB，单位是字节
    public int logsBytesPerPackage = 5 * 1024 * 1024;
    //单个 producer 实例可以使用的内存的上限，单位是字节
    public int memPoolSizeInByte = 1000 * 1024 * 1024;
    //IO 线程池最大线程数量，主要用于发送数据到日志服务
    public int maxIOThreadSizeInPool = 50;
    //当使用指定 shardhash 的方式发送日志时，这个参数需要被设置，否则不需要关心。后端 merge 线程会将映射到同一个 shard 的数据 merge 在一起，而 shard 关联的是一个 hash 区间，
    //producer 在处理时会将用户传入的 hash 映射成 shard 关联 hash 区间的最小值。每一个 shard 关联的 hash 区间，producer 会定时从从 loghub 拉取，该参数的含义是每隔 shardHashUpdateIntervalInMS 毫秒，更新一次 shard 的 hash 区间。
    public int shardHashUpdateIntervalInMS = 10 * 60 * 1000;
    //如果发送失败，重试的次数，如果超过该值，就会将异常作为 callback 的参数，交由用户处理。
    public static int retryTimes = 3;

    public SLSProducerConfig () {

    }
}
