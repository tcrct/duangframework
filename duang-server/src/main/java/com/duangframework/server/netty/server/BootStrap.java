package com.duangframework.server.netty.server;

import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.interfaces.IContextLoaderListener;
import com.duangframework.core.interfaces.IProcess;
import com.duangframework.core.kit.ThreadPoolKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.server.utils.EpollSupport;
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
import java.io.File;
import java.io.FilenameFilter;
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
    private boolean isDebug;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    protected ByteBufAllocator allocator;
    private SslContext sslContext;
    private int idleTimeInSeconds = ServerConfig.IDLE_TIME_SECONDS;
    private int bockLog = ServerConfig.SO_BACKLOG;
    private static BootStrap _bootStrap;
    private long startTimeMillis = 0;
    private IProcess mainProcess;
    private IContextLoaderListener iContextLoaderListener;

    public static BootStrap getInstants() {
        return _bootStrap;
    }

    public BootStrap(String host, int port) {
        this.host = host;
        this.port = port;
        this.startTimeMillis = System.currentTimeMillis();
        init();
        _bootStrap = this;
    }

    private void init() {
//        loadLibrary();
        try {
            bossGroup = EventLoopGroupFactory.builderBossLoopGroup();
            workerGroup = EventLoopGroupFactory.builderWorkerLoopGroup();
            allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        } catch (Exception e) {
            throw new EmptyNullException(e.getMessage(), e);
        }
    }
    private void loadLibrary() {
        String libPath = System.getProperty("dunagframework.lib.path");
        if(ToolsKit.isEmpty(libPath)) {
            return;
        }
        File libDir = new File(libPath);
        if(!libDir.isDirectory()) {
            System.out.println("lib path["+libDir+"] is not exist");
            return;
        }
        String[] files = libDir.list(new FilenameFilter(){
            @Override
            public boolean accept(File file, String name) {
                String fileName = file.getName();
                if(fileName.endsWith(".jar") && !fileName.endsWith("-sources.jar")) {
                    return file.isFile();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        });
        if(ToolsKit.isEmpty(files)) {
            return;
        }
        for(String fileName : files) {
            System.out.println(libPath+"/" + fileName);
            String path = libDir + ((fileName.startsWith("/")) ? fileName.substring(1, fileName.length()) : fileName);
            System.loadLibrary(path);
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
        if (EpollSupport.isSupportEpoll()) {
            return EpollServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public long getStartTimeMillis() {
        return System.currentTimeMillis() - startTimeMillis;
    }

    public ChannelHandler getLoggingHandler() {
        return new LoggingHandler(LogLevel.WARN);
    }

    @Override
    public void close() {
        try {
            if (null != workerGroup) {
                workerGroup.shutdownGracefully();
            }

            if (null != bossGroup) {
                bossGroup.shutdownGracefully();
            }

            if (null != allocator) {
                allocator = null;
            }

            ThreadPoolKit.shutdown();
            logger.warn("server shutdown is done!");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    public void setStartContextListener(IContextLoaderListener listener) {
        iContextLoaderListener = listener;
    }

    public void startContextListener() {
        iContextLoaderListener.start();
    }

    public IProcess getMainProcess() {
        return mainProcess;
    }

    public void setMainProcess(IProcess mainProcess) {
        this.mainProcess = mainProcess;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }
}
