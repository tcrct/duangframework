//package com.duangframework.mvc.core.helper;
//
//import com.duangframework.core.annotation.ioc.Import;
//import com.duangframework.core.kit.ObjectKit;
//import com.duangframework.core.kit.ToolsKit;
//import com.duangframework.core.utils.BeanUtils;
//import com.duangframework.core.utils.ClassUtils;
//import com.duangframework.mongodb.MongoDao;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
///**
// * IOC注入类
// * @author Created by laotang
// * @date on 2017/11/16.
// */
//public class IocHelper2 {
//
//    private static Logger logger = LoggerFactory.getLogger(IocHelper.class);
//
//    private static final Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName();
//
//    public static void duang() throws Exception {
//        // 取出所有类
//        Map<String, Class<?>> classMap = ClassUtils.getAllClassMap();
//
//        if(ToolsKit.isEmpty(classMap)) { return; }
//
//        /**
//         * 遍历所有存在beanMap里的类对象
//         * 先根据类方法是否有代理注解，如果有，则先将该类对类转换成代理类后再重新设置到beanMap里
//         * 再根据类属性是否有注入注解，如果有，则将类对象注入到属性
//         */
//        for(Iterator<Map.Entry<String, Class<?>>> it = classMap.entrySet().iterator(); it.hasNext();) {
//            Map.Entry<String, Class<?>> entry = it.next();
//            Class<?> beanClass = entry.getValue();
//            Object beanObj = BeanUtils.getBean(beanClass);
//            if(ToolsKit.isEmpty(beanObj)) {
//                continue;
//            }
//            ioc(beanObj);
//        }
//        logger.debug("IocHelper Success...");
//    }
//
//    /**
//     * 依赖注入
//     * @param beanObj
//     * @throws Exception
//     */
//    public static void ioc(Object beanObj) throws Exception {
//        Class<?> beanClass = beanObj.getClass();
//        Field[] fields = beanClass.getDeclaredFields();
//        for(Field field : fields) {
//            if (field.isAnnotationPresent(Import.class)) {
//                Class<?> fieldTypeClass = field.getType();
//                Object iocObj = null;
//                if(fieldTypeClass.equals(MongoDao.class)) {
//                    ParameterizedType paramType = (ParameterizedType) field.getGenericType();
//                    Type[] types = paramType.getActualTypeArguments();
//                    if(ToolsKit.isNotEmpty(types)) {
//                        // <>里的泛型类
//                        iocObj = BeanUtils.getBean(types[0].getClass());
//                    }
//                } else {
//                    iocObj = BeanUtils.getBean(fieldTypeClass);
//                }
//                System.out.println(iocObj+"         "+field.getName() +"                 "+beanClass.getName()+"                            "+field.getType().getCanonicalName()+"          "+fieldTypeClass.getSimpleName());
//                if(ToolsKit.isNotEmpty(iocObj)) {
//                    field.setAccessible(true);
//                    field.set(beanObj, iocObj);
//                }
//            }
//        }
//    }
//}
