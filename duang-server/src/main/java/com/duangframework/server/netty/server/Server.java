package com.duangframework.server.netty.server;


import com.duangframework.core.exceptions.ServerStartUpException;
import com.duangframework.core.kit.ThreadPoolKit;

//import com.duangframework.rpc.server.RpcServer;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.OS;
import com.duangframework.server.IServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.IOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

/**
 * @author Created by laotang
 * @date on 2017/12/8.
 */
public class Server implements IServer {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private String host;
    private int httpPort;
    private int rpcPort;
    private HttpServer httpServer;
//    private RpcServer rpcServer;

    public Server(int port) {
        this("0.0.0.0", port);
    }

    public Server(String host, int port) {
        this.host = host;
        this.httpPort = port;
        this.rpcPort = -1;
    }

    public Server(String host, int port, int rpcPort) {
        this.host = host;
        this.httpPort = port;
        this.rpcPort = rpcPort;
    }

    @Override
    public void start() {

        ThreadPoolKit.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    httpServer = new HttpServer(host, httpPort);
                    httpServer.start();
                    writePidFile();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                    throw new ServerStartUpException(e.getMessage());
                }
            }
        });

//        if(rpcPort > -1) {
//        ThreadPoolKit.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    rpcServer = new RpcServer(host, rpcPort);
//                    rpcServer.start();
//                } catch (Exception e) {
//                    logger.warn(e.getMessage(), e);
//                    throw new ServerStartUpException(e.getMessage());
//                }
//            }
//        });
//        }

    }

    @Override
    public void shutdown() {
        if(null != httpServer) {
            httpServer.shutdown();
            clearPidFile();
        }
//        if(null != rpcServer) {
//            rpcServer.shutdown();
//        }
    }

    private static String pidFile() {
        String pidFile = System.getProperty("pidfile");
        if (ToolsKit.isEmpty(pidFile)) {
            pidFile = "duang.pid";
        }
        return pidFile;
    }

    private static void writePidFile() {
        String pidFile = pidFile();
        OS os = OS.get();
        String pid= "";
        if (os.isLinux()) {
            File proc_self = new File("/proc/self");
            if(proc_self.exists()) {
                try {
                    pid = proc_self.getCanonicalFile().getName();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            File bash = new File("/bin/sh");
            if(bash.exists()) {
                ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c","echo $PPID");
                try {
                    Process p = pb.start();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    pid = rd.readLine();
                } catch(IOException e) {
                    pid = String.valueOf(Thread.currentThread().getId());
                }
            }
        } else {
            try {
                // see http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
                String name = ManagementFactory.getRuntimeMXBean().getName();
                int pos = name.indexOf('@');
                if (pos > 0) {
                    pid = name.substring(0, pos);
                } else {
                    logger.warn("Write pid file not supported on non-linux system");
                    return;
                }
            } catch (Exception e) {
                logger.warn("Write pid file not supported on non-linux system");
                return;
            }
        }
        try {
            clearPidFile();
            FileUtils.writeStringToFile(new File(pidFile), pid, Charset.forName("UTF-8"));
//            Runtime.getRuntime().addShutdownHook(new Thread() {
//                @Override
//                public void run() {
//                    clearPidFile();
//                }
//            });
        } catch (Exception e) {
            logger.warn("Error writing pid file: %s", e.getMessage(), e);
        }
    }

    private static void clearPidFile() {
        String pidFile = pidFile();
        try {
            File file = new File(pidFile);
            if (!file.delete()) {
                file.deleteOnExit();
            }
        } catch (Exception e) {
            logger.warn("Error delete pid file: %s", pidFile, e);
        }
    }

}
