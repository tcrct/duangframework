package com.duangframework.core.utils;

import com.duangframework.core.annotation.db.Entity;
import com.duangframework.core.kit.ToolsKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by laotang on 2017/11/12 0012.
 */
public class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    private static final ConcurrentMap<String, Field[]> FIELD_MAPPING_MAP = new ConcurrentHashMap<String, Field[]>();

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    public static <T> T newInstance(Class<?> clazz) {
        try {
            logger.debug("\t>>{}", clazz.getCanonicalName());
            System.out.println(clazz.getCanonicalName());
            return (T)loadClass(clazz.getCanonicalName(), true).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        if (ToolsKit.isEmpty(className)) {
            return null;
        }
        Class<?> cls;
        try {
            if (isInitialized) {
                cls = Class.forName(className, isInitialized, getClassLoader());
            } else {
                cls = getClassLoader().loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Load class is error:" + className, e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isExtends(Class<?> cls, String topClassName) {
        String clsName = cls.getCanonicalName();
        if("java.lang.Object".equals(clsName)) return true;
        Class parent = cls.getSuperclass();
        if(ToolsKit.isNotEmpty(parent)){
            String name = parent.getCanonicalName();
            if(name.equals(topClassName)) return true;
            while(ToolsKit.isNotEmpty(parent)){
                parent = parent.getSuperclass();
                if(parent == null) return false;
                name = parent.getCanonicalName();
                if(name.equals(topClassName)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 取得Bean的名字,如果有指定则用指定的,没有则用小写的类名作表名用
     * @param cls
     * @return
     */
    public static String getEntityName(Class<?> cls) {
        return getEntityName(cls, false);
    }

    /**
     * 取类的简短名称
     * @param cls			类对象
     * @param isLowerCase	是否返回小写,true时返回小写
     * @return				简短名称
     */
    public static String getClassSimpleName(Class<?> cls, boolean isLowerCase) {
        String name = cls.getSimpleName();
        return isLowerCase ? name.toLowerCase() : name;
    }

    public static String getEntityName(Class<?> cls, boolean isLowerCase) {
        Entity entity = cls.getAnnotation(Entity.class);
        String name= ( null == entity )? getClassSimpleName(cls, isLowerCase) : entity.name();
        return isLowerCase ? name.toLowerCase() : name;
    }

    /**
     * 取出类的全名，包括包名
     * @param cls                       类
     * @param isLowerCase       是否转为小写
     * @return
     */
    public static String getClassName(Class<?> cls, boolean isLowerCase) {
        String name = cls.getName();
        return isLowerCase ? name.toLowerCase() : name;
    }

    /**
     * 取出类的全名，包括包名
     * @param cls
     * @return
     */
    public static String getClassName(Class<?> cls) {
        return getClassName(cls ,true);
    }

    /**
     * 根据class对象反射出所有属性字段，静态字段除外
     * @param cls
     * @return
     */
    public static Field[] getFields(Class<?> cls){
        String key = getClassName(cls);
        Field[] field = null;
        if(FIELD_MAPPING_MAP.containsKey(key)){
            field = FIELD_MAPPING_MAP.get(key);
        }else{
            field = getAllFields(cls);
            FIELD_MAPPING_MAP.put(key, field);
        }
        return (null == field) ? null : field;
    }

    /**
     * 取出类里的所有字段
     * @param cls
     * @return	Field[]
     */
    private static Field[] getAllFields(Class<?> cls) {
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(filterStaticFields(cls.getDeclaredFields()));
        Class<?> parent = cls.getSuperclass();
        //查找父类里的属性字段
        while(null != parent && parent != Object.class){
            fieldList.addAll(filterStaticFields(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * 过滤静态方法
     * @param fields
     * @return
     */
    private static List<Field> filterStaticFields(Field[] fields){
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if(!Modifier.isStatic(field.getModifiers())){		//静态字段不取
                field.setAccessible(true);	//设置可访问私有变量
                result.add(field);
            }
        }
        return result;
    }


    /**
     * 所有Class Bean集合， 这里的所有是指@DefaultClassTemplate扫描后且实例化后的
     * key为MVC_ANNOTATION_SET的每一个元素
     * value为对应的Class
     * 当key为Controller时，具体内容为： Map<"Controller", Map<ControllerName, ControllerClass>>
     */
    private static Map<String,Map<String,Class<?>>> allClassMaps = new HashMap<>();
    public static Map<String,Map<String,Class<?>>> getAllClassMaps() {
        return allClassMaps;
    }
    public static void setAllClassMaps(String key, Map<String, Class<?>> allBeanMap) {
        allClassMaps.put(key ,allBeanMap);
        setAllClassMap(allBeanMap);
    }

    /**
     * 所有Class 集合， 这里的所有是指@DefaultClassTemplate扫描后的
     * key为 className
     * value为class
     */
    private static Map<String,Class<?>> allClassMap = new HashMap<>();
    public static Map<String,Class<?>> getAllClassMap() {
        return allClassMap;
    }
    public static void setAllClassMap(Map<String, Class<?>> allBeanMap) {
        allClassMap.putAll(allBeanMap);
    }
}
