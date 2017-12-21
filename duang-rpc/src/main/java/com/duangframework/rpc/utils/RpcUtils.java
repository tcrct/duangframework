package com.duangframework.rpc.utils;

import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.rpc.common.RpcAction;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;

/**
 * RPC工具类
 * @author Created by laotang
 * @date on 2017/12/14.
 */
public class RpcUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcUtils.class);

    /**
     *
     * @return
     */
    public static String getHost() {
        return ConfigKit.duang().key("rpc.host").defaultValue("0.0.0.0").asString();
    }

    /**
     *
     * @return
     */
    public static int getPort() {
        return ConfigKit.duang().key("rpc.port").defaultValue(9091).asInt();
    }

    /**
     *
     * @return
     */
    public static String getProductCode() {
        String productCode = ConfigKit.duang().key("product.code").asString();
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
        String path =  getZookNotePath(RpcUtils.getProductCode()) + "/" + getZKNoteItemDir();
        try {
            return URLEncoder.encode(path, Const.ENCODING_FIELD);
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
        String requestId = ToolsKit.getRequestId();
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
        String[] endPortArray = ConfigKit.duang().key("rpc.endport").asArray(); //endport的格式为 ip:port,
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
}
