package com.duangframework.server.netty.server;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.server.utils.NativeSupport;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * @author laotang
 * @date 2017/11/4
 */
public class BootStrap implements Closeable {

    private static Logger logger = LoggerFactory.getLogger(BootStrap.class);

    private String host;
    private int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    protected ByteBufAllocator allocator;
    private SslContext sslContext;
    private int idleTimeInSeconds = ServerConfig.IDLE_TIME_SECONDS;
    private int bockLog = ServerConfig.SO_BACKLOG;
    private static BootStrap _bootStrap;

    public static BootStrap getInstants() {
        return _bootStrap;
    }

    public BootStrap(String host, int port) {
        this.host = host;
        this.port = port;
        init();
        _bootStrap = this;
    }

    private void init() {
        try {
            bossGroup = EventLoopGroupFactory.builderBossLoopGroup();
            workerGroup = EventLoopGroupFactory.builderWorkerLoopGroup();
            allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        } catch (Exception e) {
            throw new EmptyNullException(e.getMessage(), e);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBockLog(int bockLog) {
        this.bockLog = bockLog;
    }

    public int getBockLog() {
        return bockLog;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public ByteBufAllocator getAllocator() {
        return allocator;
    }

    public void setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public boolean isSslEnabled() {
        return sslContext != null;
    }

    public void setSslContext(SslContext sslContext) {
//        sslContext = SslKit.buildServerSsl(certFile, keyFile);
        this.sslContext = sslContext;
    }

    public int getIdleTimeInSeconds() {
        return idleTimeInSeconds;
    }

    public void setIdleTimeInSeconds(int idleTimeInSeconds) {
        this.idleTimeInSeconds = idleTimeInSeconds;
    }

    public InetSocketAddress getSockerAddress() {
        return new InetSocketAddress(host, port);
    }

    public Class<? extends ServerChannel> getDefaultChannel() {
        if (NativeSupport.isSupportNative()) {
            return EpollServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public ChannelHandler getLoggingHandler() {
        return new LoggingHandler(LogLevel.WARN);
    }

    @Override
    public void close() throws IOException {
        if(null != workerGroup) {
            workerGroup.shutdownGracefully();
        }

        if(null != bossGroup) {
            bossGroup.shutdownGracefully();
        }
    }
}
