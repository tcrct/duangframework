package com.duangframework.rpc.utils;

import com.duangframework.core.annotation.rpc.RpcPackage;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.exceptions.RpcException;
import com.duangframework.core.kit.PropertiesKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.rpc.common.RpcAction;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * RPC工具类
 * @author Created by laotang
 * @date on 2017/12/14.
 */
public class RpcUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcUtils.class);
    private static String RPC_PACKAGE_PATH = "";

    /**
     *
     * @return
     */
    public static String getHost() {
        return PropertiesKit.duang().key("rpc.host").defaultValue("0.0.0.0").asString();
    }

    /**
     *
     * @return
     */
    public static int getPort() {
        return PropertiesKit.duang().key("rpc.port").defaultValue(9091).asInt();
    }

    /**
     *
     * @return
     */
    public static String getProductCode() {
        String productCode = PropertiesKit.duang().key("product.code").asString();
        if(ToolsKit.isEmpty(productCode)) {
            throw new EmptyNullException("product.code is null");
        }
        return productCode;
    }



    /**
     * 取该产品代码在ZK上的全路径
     * 路径规则：
     *  /root/duangframework/rpc / 运行环境 / 产品代号 / 内网IP_端口
     * @return
     */
    public static String getZookNoteFullPath() {
        try {
            return getZookNotePath(RpcUtils.getProductCode()) + "/" + getZKNoteItemDir();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 取出该产品代码在ZK上的根目录路径
     * @return
     */
    public static String getZookNotePath(String productCode) {
        return  Const.RPC_ROOT_PATH + "/" + ToolsKit.getUseEnv() + "/" + productCode;
    }
    /**
     * 根据IP与端口确定目录
     * @return
     */
    private static String getZKNoteItemDir() {
        return IpUtils.getLocalHostIP(false) +"_"+getPort();
    }

    /**
     * 取请求ID
     * @return
     */
    public static String getRequestId() {
        String requestId = ToolsKit.getThreadLocalDto().getRequestId();
        if(ToolsKit.isEmpty(requestId)) {
            //已经出错,但为了程序正常运行，只有NEW一个新的ID
            requestId = new DuangId().toString();
            logger.warn("get duang requestId is fail");
        }
        return requestId;
    }

    /**
     * 如果有指定rpc.endport的话，则根据指定的endpoint地址去取对应的节点数据返回，一般用于测试开发
     * @param actionMap
     * @return
     */
    public static Map<String, List<RpcAction>> getAssignRpcActionMap(Map<String, List<RpcAction>> actionMap) {
        String[] endPortArray = PropertiesKit.duang().key("rpc.endport").asArray(); //endport的格式为 ip:port,
        if(ToolsKit.isEmpty(endPortArray)) { return null; }
        List endPortList = Arrays.asList(endPortArray);
        Map<String, List<RpcAction>> newActionMap = new HashMap<String, List<RpcAction>>();
        for(Iterator<Map.Entry<String,List<RpcAction>>> it = actionMap.entrySet().iterator(); it.hasNext();){
            Map.Entry<String,List<RpcAction>> entry = it.next();
            List<RpcAction> actionList = entry.getValue();
            List<RpcAction> actionListNew = new ArrayList<>();
            if(ToolsKit.isNotEmpty(actionList)) {
                for (RpcAction action : actionList) {
                    String publicEndPort = action.getRemoteip() + ":" + action.getPort();
                    String intranetEndPort = action.getIntranetip() + ":" + action.getPort();
                    if( endPortList.contains(publicEndPort) ||
                            endPortList.contains(intranetEndPort) ||
                            endPortList.contains(action.getRemoteip()) ||
                            endPortList.contains(action.getIntranetip())) {
                        actionListNew.add(action);
                    }
                }
            }
            if(ToolsKit.isNotEmpty(actionListNew)) {
                newActionMap.put(entry.getKey(), actionListNew);
            }
        }
        return ToolsKit.isEmpty(newActionMap) ? null : newActionMap;
    }

    /**
     * 根据传入的size数取小于(size-1)的随机数
     *
     * @param size
     * @return
     */
    public static int getRandomBySize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size参数不正确");
        }
        return (int) (Math.ceil(Math.random() * size) - 1);
    }

    public static String formatDate(long dateTime) {
        return DateFormatUtils.format(dateTime, Const.DEFAULT_DATE_FORM);
    }


    public static String createRpcClientKey(String ip, int port) {
//        return Base64.encode(Unpooled.copiedBuffer((ip+port).getBytes())).toString();
        return ip+"_"+port;
    }

    /**
     * 自动创建Service类的接口文件
     * RPC模块文件夹路径，即是接口文件存在的父目录
     */
    public static void autoCreateBatchInterface() throws Exception {
        try {
            // 自定义目录
            String customizeDir = PropertiesKit.duang().key("rpc.module.customdir").defaultValue("provider").asString();
            AutoBuildServiceInterface.createBatchInterface(RpcUtils.getRpcModulePath(customizeDir), customizeDir);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    /**
     * 接品文件
     * @param productCode
     * @return
     */
    public static String getInterFaceJavaPath(String productCode) {
        return getZookNotePath(productCode) + "/interface";
    }

    /**
     * 生成消费者端的接口文件
     * @param interFaceDirPath      接口文件的父级目录
     * @param packageStr               包路径，包括文件名
     * @param fileContext               内容
     * @return
     * @throws Exception
     */
    public static void createInterFaceFileOnDisk(String interFaceDirPath, String packageStr, String fileContext) throws Exception {
        String fileName = packageStr.substring(packageStr.lastIndexOf(".")+1) + ".java";
        AutoBuildServiceInterface.createInterFaceFileOnDisk(interFaceDirPath, fileName, fileContext);
    }

    /**
     * 取得RPC模块的全路径
     * @param flag      自定义目录
     * @return
     */
    public static String getRpcModulePath(String customizeDir) {
        String rpcModulePath = PropertiesKit.duang().key("rpc.module.path").asString();
        rpcModulePath = rpcModulePath.endsWith("/") ? rpcModulePath.substring(0, rpcModulePath.length()-1) : rpcModulePath;
        return rpcModulePath + "/" +getRpcPackagePath("").replace(".","/")+(ToolsKit.isNotEmpty(customizeDir) ? "/" +customizeDir  : "");
    }


    public static String getRpcPackagePath(String flag) {
        if(ToolsKit.isEmpty(RPC_PACKAGE_PATH)) {
            String basePackagePath = PropertiesKit.duang().key("base.package.path").asString();
            Package[] pkgs = Package.getPackages();
            for (Package pkg : pkgs) {
                if (pkg.getName().contains(basePackagePath)) {
                    RpcPackage rpcPackage = pkg.getAnnotation(RpcPackage.class);
                    if (null != rpcPackage) {
                        RPC_PACKAGE_PATH = pkg.getName();
                    }
                }
            }
        }
        return RPC_PACKAGE_PATH + (ToolsKit.isNotEmpty(flag) ? "." +flag  : "");
    }
}
