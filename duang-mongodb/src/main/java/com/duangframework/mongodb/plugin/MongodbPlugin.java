package com.duangframework.mongodb.plugin;

import com.duangframework.core.annotation.ioc.Import;
import com.duangframework.core.interfaces.IPlugin;
import com.duangframework.core.kit.ConfigKit;
import com.duangframework.core.kit.ToolsKit;
import com.duangframework.core.utils.BeanUtils;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.mongodb.MongoDao;
import com.duangframework.mongodb.common.MongoConnect;
import com.duangframework.mongodb.kit.MongoClientKit;
import com.duangframework.mongodb.utils.MongoUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * MongoDB插件
 * @author Created by laotang
 * @date on 2017/11/20.
 */
public class MongodbPlugin implements IPlugin {

    @Override
    public void init() throws Exception {
        // 可以初始一些值，框架先执行init方法后再执行start
    }

    @Override
    public void start() throws Exception {
        MongoClientKit.duang().connect(new MongoConnect(
                ConfigKit.duang().key("mongodb.host").defaultValue("127.0.0.1").asString(),
                ConfigKit.duang().key("mongodb.port").defaultValue("27017").asInt(),
                ConfigKit.duang().key("mongodb.databasename").defaultValue("local").asString(),
                ConfigKit.duang().key("mongodb.username").defaultValue("").asString(),
                ConfigKit.duang().key("mongodb.password").defaultValue("").asString(),
                ConfigKit.duang().key("mongodb.replicaset").asList()
        )).getClient();

        importDao();
    }

    @Override
    public void stop() throws Exception {
        if(null != MongoClientKit.duang().getClient()) {
            MongoClientKit.duang().getClient().close();
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
                if (field.isAnnotationPresent(Import.class) && MongoDao.class.equals(field.getType())) {
                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
                    Type[] types = paramType.getActualTypeArguments();
                    if(ToolsKit.isNotEmpty(types)) {
                        // <>里的泛型类
                        String paramTypeClassName = types[0].toString().substring(6).trim();
                        Class<?> paramTypeClass  = ClassUtils.loadClass(paramTypeClassName, false);
                        Object daoObj = MongoUtils.getMongoDao(paramTypeClass);
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
}
