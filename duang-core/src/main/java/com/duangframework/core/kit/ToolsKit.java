package com.duangframework.core.kit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.duangframework.core.annotation.db.Id;
import com.duangframework.core.common.Const;
import com.duangframework.core.common.DuangId;
import com.duangframework.core.common.IdEntity;
import com.duangframework.core.common.dto.result.HeadDto;
import com.duangframework.core.common.dto.result.ReturnDto;
import com.duangframework.core.common.enums.IEnums;
import com.duangframework.core.exceptions.EmptyNullException;
import com.duangframework.core.utils.ClassUtils;
import com.duangframework.core.utils.DuangThreadLocal;
import com.duangframework.core.utils.IpUtils;
import com.duangframework.core.utils.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by laotang on 2017/10/31.
 */
public class ToolsKit {

    private static Logger logger = LoggerFactory.getLogger(ToolsKit.class);

    private static SerializeConfig jsonConfig = new SerializeConfig();

    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // 定义一个请求对象安全线程类
    private static DuangThreadLocal<HeadDto> requestHeaderThreadLocal = new DuangThreadLocal<HeadDto>() {
        @Override
        public HeadDto initialValue() {
            return new HeadDto();
        }
    };
    private static SerializerFeature[] serializerFeatureArray = {
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.NotWriteRootClassName
    };

    private static SerializerFeature[] serializerFeatureArray2 = {
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.NotWriteRootClassName
    };

    static {
        jsonConfig.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss.SSS"));
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
        return JSON.toJSONString(obj, jsonConfig, serializerFeatureArray);
    }

    public static String toJsonString2(Object obj) {
        return JSON.toJSONString(obj, jsonConfig, serializerFeatureArray2);
    }

    public static byte[] toJsonBytes(Object obj) {
        return JSON.toJSONBytes(obj, jsonConfig, serializerFeatureArray);
    }

    public static <T> T jsonParseObject(String jsonText, Class<T> clazz) {
        return JSON.parseObject(jsonText, clazz);
    }

    public static <T> T jsonParseObject(String jsonText, Type typeClazz) {
        return JSON.parseObject(jsonText, typeClazz);
    }

    public static <T> T jsonParseObject(String jsonText, TypeReference<T> typeReference) {
        return JSON.parseObject(jsonText, typeReference);
    }

    public static <T> List<T> jsonParseArray(String jsonText, Class<T> clazz) {
        return JSON.parseArray(jsonText, clazz);
    }

    public static <T> T jsonParseObject(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    public static <T> T xmlParseObject(String xmlText, Class<T> clazz) {
        String json = toJsonString(XmlHelper.of(xmlText).toMap());
        return jsonParseObject(json, clazz);
    }

    public static String getCurrentDateString() {
        try {
            return SDF.format(new Date());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 关键字是否存在于map中, 如果存在返回true, 不存在返回false
     *
     * @param key
     * @param map
     * @return
     */
    public static boolean isExist(String key, Map map) {
        if (map.containsKey(key)) {
            return true;
        }
        return false;
    }


    /**
     * 如果是10开头的IP，统统默认为阿里云的机器
     * @return
     */
    public static boolean isAliyunHost() {
        String  clientIp = IpUtils.getLocalHostIP(false).trim();
        if(isEmpty(clientIp)) throw new EmptyNullException("getLocalHostIP Fail: Ip is Empty!");
        String preFixIp = ConfigKit.duang().key("ip.prefix").defaultValue("10").asString();
        return  clientIp.startsWith(preFixIp) ? true : false;
    }


    /**
     * 使用环境，分内测(local)，外测(obt)，正式(api), 如果不设置，默认为api
     * @return
     */
    public static String getUseEnv() {
        if (ToolsKit.isEmpty(Const.USE_ENV)) {
            String ip = IpUtils.getLocalHostIP();
            String fatIp = "", uatIp = "";
            try { fatIp = getEnvIp("fat.ips"); } catch (Exception e) { logger.warn(e.getMessage(), e); }
            try { uatIp = getEnvIp("uat.ips"); } catch (Exception e) { logger.warn(e.getMessage(), e); }
            if (ip.startsWith("192.168") || ip.startsWith("127")) {
                Const.USE_ENV = "dev";
            } else if (Arrays.asList(fatIp.split(",")).contains(ip)) {
                Const.USE_ENV = "fat";  // fat
            } else if (Arrays.asList(uatIp.split(",")).contains(ip)) {
                Const.USE_ENV = "uat";  // uat
            } else {
                Const.USE_ENV = "pro";
            }
        }
        return Const.USE_ENV;
    }

    private static String getEnvIp(String envKey) {
        String ipAddress = "";
        try {
            ipAddress = ConfigKit.duang().key(envKey).asString();
        } catch (Exception e) {
            ipAddress = PropertiesKit.duang().key(envKey).asString();
        }
        logger.warn("getEnvIp: " + ipAddress);
        if(ToolsKit.isEmpty(ipAddress)) {
            throw new EmptyNullException("取环境机器IP时失败，请先在配置文件里设置: " + envKey);
        }
        return ipAddress;
    }

    public static DuangId message2DuangId(String id) {
        boolean isObjectId = ToolsKit.isValidDuangId(id);
        if (isObjectId) {
            return new DuangId(id);
        } else {
            throw new IllegalArgumentException(id + " is not Vaild DuangId");
        }
    }

    /**
     * 验证是否为MongoDB 的ObjectId
     *
     * @param str
     *            待验证字符串
     * @return  如果是则返回true
     */
    public static boolean isValidDuangId(String str) {
        if (ToolsKit.isEmpty(str)) {
            return false;
        }
        int len = str.length();
        if (len != 24) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if ((c < '0') || (c > '9')) {
                if ((c < 'a') || (c > 'f')) {
                    if ((c < 'A') || (c > 'F')) {
                        logger.warn(str + " is not DuangId!!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 根据format字段，格式化日期
     * @param date          日期
     * @param format        格式化字段
     * @return
     */
    public static String formatDate(Date date, String format) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     *  将字符串日期根据format格式化字段转换成日期类型
     * @param stringDate    字符串日期
     * @param format           格式化日期
     * @return
     */
    public static Date parseDate(String stringDate, String format) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        try {
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 设置请求头DTO到ThreadLocal变量
     * @param headDto       请求头DTO
     */
    public static void setThreadLocalDto(HeadDto headDto) {
        requestHeaderThreadLocal.set(headDto);
    }

    /**
     *  取ThreadLocal里的HeadDto对象
     * @return
     */
    public static HeadDto getThreadLocalDto() {
        return  requestHeaderThreadLocal.get();
    }

    /**
     * 控制台打印信息
     * @param message       要打印的信息
     */
    public static void console(Object message) {
        if(ConfigKit.duang().key("debug").defaultValue(false).asBoolean()) {
            if(message instanceof String) {
                System.out.println(message);
            } else {
                System.out.println(toJsonString(message));
            }
        }
    }

    /**
     *
     * @param enums
     * @param obj
     * @return
     */
    public static ReturnDto<Object> buildReturnDto(IEnums enums, Object obj) {
        ReturnDto<Object> dto = new ReturnDto<Object>();
        HeadDto head = getThreadLocalDto();
        if(isEmpty(head)) {
            head = new HeadDto();
        }
        if (ToolsKit.isEmpty(enums)) {
            head.setRet(IEnums.IENUMS_SUCCESS_CODE);
            head.setMsg(IEnums.IENUMS_SUCCESS_MESSAGE);
        } else {
            head.setRet(enums.getCode());
            head.setMsg(enums.getMessage());
        }
        dto.setHead(head);
        dto.setData(obj);
        return dto;
    }

    public static String getFieldName(Field field) {
        JSONField jsonField = field.getAnnotation(JSONField.class);
        return (ToolsKit.isEmpty(jsonField)) ? field.getName() :
                (ToolsKit.isEmpty(jsonField.format()) ? jsonField.name() : jsonField.format());
    }

    public static String getIdFieldName(Class<?> entityClass) {
        Field idField = null;
        Field[] fields = ClassUtils.getFields(entityClass);
        for(Field field : fields) {
            if(field.isAnnotationPresent(Id.class)) {
                idField = field;
                break;
            }
        }
        return null == idField ? IdEntity.ENTITY_ID_FIELD : idField.getName();
    }

    public static FileFilter fileFilter(final File dir, final String extName){
        return new FileFilter() {
            public boolean accept(File file) {
                if(".class".equalsIgnoreCase(extName)) {
                    return ( file.isFile() && file.getName().endsWith(extName) ) || file.isDirectory();
                } else if(".jar".equalsIgnoreCase(extName)) {
                    return ( file.isFile() && file.getName().endsWith(extName) ) || file.isFile();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        };
    }
}
