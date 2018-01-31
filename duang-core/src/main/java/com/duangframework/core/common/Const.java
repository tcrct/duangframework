package com.duangframework.core.common;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *  常量类
 * @author laotang
 * @date 2017/11/15
 */
public class Const {

    /**
     * 默认的编码格式字符串
     */
    public static final String ENCODING_FIELD = "UTF-8";

    /**
     *数据库
     */
    public static final String HOST_FIELD = "host";
    public static final String PORT_FIELD = "port";
    public static final String DATABASE_FIELD = "database";
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    public static final String REPLICASET_FIELD = "replicaset";

    /**
     * 使用环境
     */
    public static String USE_ENV = "local";

    public static final String DUANG_REQUEST_ID = "duang-request-id";
    public static final String REMOTE_CLIENT_IP  = "duang-client-ip";
    public static final String REMOTE_SERVICE_IP  = "duang-service-ip";
    public static final String RENDER_TYPE_FILED  = "duang-render-type";
    public static final String OWNER_FILED  = "duang-x-owenr";
    public static final String OWNER  = "duangframework";

    /**
     * 请求超时默认时间，3秒
     */
    public static final long REQUEST_TIMEOUT = 3000L;


    /**
     * RPC父目录
     */
    public static final String RPC_ROOT_PATH = "/root/duangframework/rpc";

    /**
     * 日期格式
     */
    public static final String DEFAULT_DATE_FORM = "yyyy-MM-dd HH:mm:ss";

    /**
     *  以JSON或XML方式提交参数时，暂存在Request里的key
     */
    public static final String DUANG_INPUTSTREAM_STR_NAME = "duang_inputstream_str";


    /**
     * 配置中心客户端类名，全路径
     */
    public static final String CONFIG_CLIENTCLASS_NAME= "config.client.name";

    /**
     * 配置中心容器路径，即注册中心的父目录
     */
    public static final String CONFIG_CONTAINER_PATH= "config.container.path";

    /**
     * 配置中心枚举文件全路径
     */
    public static final String CONFIG_ENUMS_PATH= "config.enums.path";

    /**
     * 默认的注册中心父目录路径
     */
    public static final String CONFIG_CONTAINER_PATH_VALUE = "";

    /**
     * 节点名称，与上面的父目录路径组合成一个完成的路径
     */
    public static final String CONFIG_NODENAMES = "config.node.names";


    public final static String FORWARDED_FOR= "X-Forwarded-For";
    public final static String FORWARDED_PROTO = "X-Forwarded-Proto";
    public final static String REAL_IP = "X-Real-IP";

    /**
     * 所有指定扫描类的URL集合
     */
    public final static List<URL> URL_LIST = new ArrayList<>();

   /**
     * 是否重复扫描, 用于热加载
     */
    public static boolean IS_RELOAD_SCANNING = false;

    /**
     *系统用户ID
     */
    public static String DUANG_SYSTEM_USERID = "57bead16c6396d11707ee174";
}
