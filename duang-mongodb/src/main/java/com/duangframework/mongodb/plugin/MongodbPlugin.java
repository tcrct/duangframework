package com.duangframework.mongodb.plugin;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoClientExt;
import com.duangframework.mongodb.common.MongoDbConnect;
import com.duangframework.mongodb.kit.MongoClientKit;
import com.duangframework.mongodb.utils.MongoUtils;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MongoDB插件
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongodbPlugin implements IPlugin {

    private final static Logger logger = LoggerFactory.getLogger(MongodbPlugin.class);
    private List<MongoDbConnect> connectList = new ArrayList<>();


    public MongodbPlugin(MongoDbConnect dbConnect) {
        this(dbConnect.getHost(), dbConnect.getPort(), dbConnect.getUserName(), dbConnect.getPassWord(), dbConnect.getDataBase(), dbConnect.getUrl(), dbConnect.getClientCode());
    }

    public MongodbPlugin(String  url) {
        this("", 0, "", "", "", url, "");
    }

    public MongodbPlugin(String  url, String exampleCode) {
        this("", 0, "", "", "", url, exampleCode);
    }

    public MongodbPlugin(String host, int port, String dataBase, String userName, String passWord, String url, String exampleCode) {
        MongoDbConnect connect = new MongoDbConnect(host, port, userName, passWord, dataBase, url, exampleCode);
        connectList.add(connect);
    }



    /**
     * 多数据库时使用
     * @param connectList
     */
    public MongodbPlugin(List<MongoDbConnect> connectList) {
        this.connectList.addAll(connectList);
    }

    @Override
    public void init() throws Exception {
        // 可以初始一些值，框架先执行init方法后再执行start
    }

    /**
     * 如果没有设置默认db client的话，则用第一个client作为默认的client
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        boolean isFirstClient = true;
        for(MongoDbConnect connect : connectList) {
            MongoClient client = MongoClientKit.duang().connect(connect).getClient();
            String key = connect.getClientCode();
            if(ToolsKit.isNotEmpty(client) && ToolsKit.isNotEmpty(key)) {
                MongoClientExt clientExt = new MongoClientExt(key, client, connect);
                if(isFirstClient) {
                    MongoUtils.setDefaultClientExt(clientExt);
                    isFirstClient = false;
                }
                if(connect.isDefaultClient()) {
                    MongoUtils.setDefaultClientExt(clientExt);
                }
                MongoUtils.setMongoClient(key, clientExt);
                connect.printDbInfo(key);
            }
        }
        importDao();
    }

    @Override
    public void stop() throws Exception {
        if(null != MongoUtils.getMongoClientExtMap()) {
            for(Iterator<Map.Entry<String, MongoClientExt>> it = MongoUtils.getMongoClientExtMap().entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, MongoClientExt> entry = it.next();
                entry.getValue().getClient().close();
            }
        }
    }

    /**
     * IOC注入MongoDao
     * @throws Exception
     */
    private void importDao() throws Exception {
    // 取出所有类对象
    Map<Class<?>, Object> allBeanMap = BeanUtils.getAllBeanMap();
    for(Iterator<Map.Entry<Class<?>, Object>> it = allBeanMap.entrySet().iterator(); it.hasNext();) {
        Map.Entry<Class<?>, Object> entry = it.next();
        Class<?> beanClass = entry.getKey();
        Field[] fields = beanClass.getDeclaredFields();
        Object beanObj = entry.getValue();
            for(Field field : fields) {
                Import importAnnot = field.getAnnotation(Import.class);
                if (ToolsKit.isNotEmpty(importAnnot) && MongoDao.class.equals(field.getType())) {
                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(types)) {
                        // <>里的泛型类
                        String paramTypeClassName = types[0].toString().substring(6).trim();
                        Class<?> paramTypeClass  = ClassUtils.loadClass(paramTypeClassName, false);
                        Object daoObj = MongoUtils.getMongoDao(importAnnot.dbclient(), paramTypeClass);
//                        BeanUtils.setBean2Map(paramTypeClass, daoObj);
                        field.setAccessible(true);
                        field.set(beanObj, daoObj);
                    }
                }
//                BeanUtils.setBean2Map(beanClass, beanObj);
            }
//        System.out.println(beanClass.getCanonicalName()+"         $$$$$$$$$         "+beanObj.getClass().getSimpleName());
//
        }
    }


    public String getMongodbUrl() throws Exception {
        String[] stringArray =  ConfigKit.duang().key("mongodb.url").asArray();
        if(ToolsKit.isNotEmpty(stringArray)) {
            StringBuffer sb = new StringBuffer();
            for(String str : stringArray) {
                sb.append(str).append(",");
            }
            if(sb.length() > 0) {
                return sb.deleteCharAt(sb.length() - 1).toString();
            }
        }
        return "";
    }
}
