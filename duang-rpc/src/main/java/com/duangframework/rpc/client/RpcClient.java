package com.duangframework.rpc.client;

import com.duangframework.core.exceptions.RpcException;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.rpc.common.*;
import com.duangframework.rpc.utils.RpcUtils;
import com.duangframework.server.netty.server.AbstractNettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Created by laotang
 * @date on 2017/12/8.
 */
public class RpcClient extends AbstractNettyServer {

    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public static RpcClient rpcClient = null;
    private final Lock lockChannelMap = new ReentrantLock();
    private static Lock lockClientChannel = new ReentrantLock();
    private static final long LockTimeoutMillis = 3000;

    private static final ConcurrentHashMap<String, ChannelWrapper> channelMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, LinkedBlockingQueue<RpcResponse>> RESPONSE_MAP = new ConcurrentHashMap<>();

    public RpcClient() {
        super("0.0.0.0", 0);
    }

    @Override
    public void start() {
        try {
            nettyBootstrap.childHandler(new ClientChannelInitializer());
        } catch (Exception e) {
            throw new RuntimeException("this.nettyBootstrap.bind().sync() InterruptedException", e);
        } finally {
            shutdown();
        }
    }

    /**
     * 重链
     * @return
     */
    public static void reConnon() {
        rpcClient = null;
        getInstance();
        logger.warn("reConnon is " + ((ToolsKit.isNotEmpty(rpcClient)) ? "success" : "fail"));
    }

    public static RpcClient getInstance() {
        if(null == rpcClient) {
            try {
                lockClientChannel.lock();
                rpcClient = new RpcClient();
                rpcClient.start();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            } finally {
                lockClientChannel.unlock();
            }
        }
        return rpcClient;
    }

    /**
     * 调用运程方法
     * @param request   rpc请求对象，封装请求的参数，参数类型等
     * @param action     rpc生产者对象，封装了生产者或者说是要调用的方法的类名，方法名，IP地址，端口等
     * @return                  处理结果 RpcResponse对象
     * @throws Exception
     */
    public RpcResponse call(RpcRequest request, RpcAction action) throws Exception {
        // 这里要用内网的IP地址
        Channel channel = getChannel(action.getIntranetip(), action.getPort());
        RpcResponse response = null;
        // 先初始化一个以请求ID为KEY的响应阵列到集合中
        final String requestId = request.getRequestId();
        // 将请求结果预存到MAP中，以请求ID为key
        RESPONSE_MAP.put(requestId, new LinkedBlockingQueue<RpcResponse>(1));
        try{
            if(channel.isOpen()) {
                MessageHolder<RpcRequest> messageHolder = new MessageHolder<RpcRequest>(Protocol.REQUEST, Protocol.OK, request);
                ChannelFuture writeFuture = channel.writeAndFlush(messageHolder).sync();
                writeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        // 如果返回正常，则直接退出
                        if (future.isSuccess()) {
                            return;
                        } else {
                            String errorMsg = "Send request[" + requestId + "] to [" + future.channel().toString() + "] is error: " + future.cause().toString();
                            throw new RpcException(errorMsg);
                        }
                    }
                });
            } else {
                logger.warn("channel.isClose: " + channel.remoteAddress());
            }
            response = getRpcResponse(requestId, request.getTimeout());
            if(null != response) {
                logger.warn("poll time: " + (System.currentTimeMillis() - response.getRequestStartTime()) + " ms");
            }
        } catch (Exception e) {
            response = createExceptionRpcResponse(action, e, requestId);
        }  finally {
            // 无论成功与否，都必须移除集合中指定KEY的队列
            RESPONSE_MAP.remove(requestId);
        }
        return response;
    }

    /**
     *取Netty Channel
     * @param targetHost    目标地址
     * @param targetPort    目标端口
     * @return  Channel
     * @throws Exception
     */
    private Channel getChannel(String targetHost, int targetPort) throws Exception {
        if(ToolsKit.isEmpty(targetHost)) {
            throw new NullPointerException("targetHost is empty!");
        }
        if(targetPort <= 0) {
            throw new NullPointerException("targetPort must gt zero!");
        }

        String key = RpcUtils.createRpcClientKey(targetHost, targetPort);
        //等待3秒，超时则放弃
        if(lockChannelMap.tryLock(LockTimeoutMillis, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                ChannelWrapper cw = channelMap.get(key);
                if (null != cw) {
                    // 校验channel的状态
                    if (cw.isOK()) {
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    } else {
                        // 如果缓存中channel的状态不正确的情况下，则将此不健康的channel从缓存中移除，重新创建
                        channelMap.remove(key);
                        createNewConnection = true;
                    }
                } else {
                    createNewConnection = true;
                }
                // 创建新的链接
                if (createNewConnection) {
                    ChannelFuture channelFuture = nettyBootstrap.bind(new InetSocketAddress(targetHost, targetPort)).sync();
                    channelFuture.awaitUninterruptibly(LockTimeoutMillis); //等待
                    logger.warn("createChannel: begin to connect remote "+targetHost+":"+targetPort+" asynchronously");  //开始异步链接
                    // 将返回的Netty对象的ChannelFuture对象编织成一个cw
                    cw = new ChannelWrapper(channelFuture);
                    if(cw.isOK()) {
                        // 放入缓存
                        channelMap.put(key, cw);
                        logger.warn("createChannel:  connect remote "+targetHost+":"+targetPort+" success");
                        return cw.getChannel();
                    }
                }
            } catch (Exception e) {
                logger.warn("getChannel: create channel exception:  "+ e.getMessage(), e);
            } finally {
                // 释放锁
                this.lockChannelMap.unlock();
            }
        } else {
            logger.warn("getChannel: try to lock channel map, but timeout, "+LockTimeoutMillis+" ms");
        }
        return null;
    }

    public static ConcurrentHashMap<String, LinkedBlockingQueue<RpcResponse>> getResponseMap() {
        return RESPONSE_MAP;
    }


    /**
     * 取RPC返回对象
     * @param key               请求ID
     * @param timeout       等待结果超时时间数
     * @return
     */
    public RpcResponse getRpcResponse(String key, long timeout) {
        try {
            if (RESPONSE_MAP.containsKey(key)) {
                return RESPONSE_MAP.get(key).poll(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            logger.warn("getRpcResponse is fail: " + e.getMessage(), e);
        }
        return new RpcResponse();
    }

    /**
     * 创建异常RpcResponse对象
     * @param action
     * @param e
     * @param requestId
     * @return
     */
    private RpcResponse createExceptionRpcResponse(RpcAction action, Exception e, String requestId) {
        String errorMsg = "["+requestId+"] RpcClient call remote["+action.getRemoteip()+":"+action.getPort()+"] function["+action.getIface()+"] is fail: " + e.getMessage();
        logger.warn(errorMsg, e);
        RpcResponse response = new RpcResponse();
        response.setRequestId(requestId);
        response.setResult(errorMsg);
        response.setError(e.getCause());
        return response;
    }
}
