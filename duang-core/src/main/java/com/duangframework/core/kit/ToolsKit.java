package com.duangframework.core.kit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.duangframework.core.utils.DuangThreadLocal;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by laotang on 2017/10/31.
 */
public class ToolsKit {

    private static SerializeConfig jsonConfig = new SerializeConfig();

    // 定义一个IdEntity安全线程类
    private static DuangThreadLocal<String> duangThreadLocal = new DuangThreadLocal<String>() {
        public String initialValue() {
            return "";
        }
    };

    static {
        jsonConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss.SSS"));
        // jsonConfig.put(Charset.class, new CharArraySerializer());
    }

    /***
     * 判断传入的对象是否为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,为空或等于0时返回true
     */
    public static boolean isEmpty(Object obj) {
        return checkObjectIsEmpty(obj, true);
    }

    /***
     * 判断传入的对象是否不为空
     *
     * @param obj
     *            待检查的对象
     * @return 返回的布尔值,不为空或不等于0时返回true
     */
    public static boolean isNotEmpty(Object obj) {
        return checkObjectIsEmpty(obj, false);
    }

    @SuppressWarnings("rawtypes")
    private static boolean checkObjectIsEmpty(Object obj, boolean bool) {
        if (null == obj)
            return bool;
        else if (obj == "" || "".equals(obj))
            return bool;
        else if (obj instanceof Integer || obj instanceof Long || obj instanceof Double) {
            try {
                Double.parseDouble(obj + "");
            } catch (Exception e) {
                return bool;
            }
        } else if (obj instanceof String) {
            if (((String) obj).length() <= 0)
                return bool;
            if ("null".equalsIgnoreCase(obj+""))
                return bool;
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0)
                return bool;
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0)
                return bool;
        } else if (obj instanceof Object[]) {
            if (((Object[]) obj).length == 0)
                return bool;
        }
        return !bool;
    }

    /**
     * 判断是否JSON字符串
     * @param jsonString
     * @return
     */
    public static boolean isMapJsonString(String jsonString) {
        return jsonString.startsWith("{") && jsonString.endsWith("}");
    }

    /**
     * 判断是否数据JSON字符串
     * @param jsonString
     * @return
     */
    public static boolean isArrayJsonString(String jsonString) {
        return jsonString.startsWith("[") && jsonString.endsWith("]");
    }

    /**
     * 判断是否是数组
     * @param obj
     * @return
     */
    public static boolean isArray(Object obj) {
        return obj instanceof List || obj instanceof Array || obj.getClass().isArray();
    }


    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj, jsonConfig, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> T jsonParseObject(String jsonText, Class<T> clazz) {
        return JSON.parseObject(jsonText, clazz);
    }

    public static <T> T jsonParseObject(String jsonText, Type typeClazz) {
        return JSON.parseObject(jsonText, typeClazz);
    }

    public static <T> List<T> jsonParseArray(String jsonText, Class<T> clazz) {
        return JSON.parseArray(jsonText, clazz);
    }

}
